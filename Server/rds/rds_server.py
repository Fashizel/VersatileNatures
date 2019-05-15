from datetime import datetime

import mysql.connector

KEY_EVENT_TIMESTAMP = 'event_timestamp'


def post_process_sensor_data(raw_sensor_data):
    """
    Add missing data and perform smoothing on sensor data
    """
    i = 0
    new_sensor_data = []
    while i < len(raw_sensor_data):
        sensor = raw_sensor_data[i]
        new_sensor_data.append(sensor)
        if i + 1 < len(raw_sensor_data):
            next_sensor = raw_sensor_data[i + 1]
            while next_sensor[KEY_EVENT_TIMESTAMP] - 1000 > sensor[KEY_EVENT_TIMESTAMP]:
                next_sensor = sensor.copy()
                next_sensor[KEY_EVENT_TIMESTAMP] += 1000
                new_sensor_data.append(next_sensor)
        i += 1

    print("Added {} missing sensor data".format(len(new_sensor_data) - len(raw_sensor_data)))

    for i in range(len(new_sensor_data) - 1):
        moving_average(new_sensor_data, i, 'weight')
        moving_average(new_sensor_data, i, 'acc_az')

    return new_sensor_data


def moving_average(raw_sensor_data, index, key):
    """
    Perform moving average on a key of type number, up to 2 look-back and 2 look-forward
    """
    mean = get_value(raw_sensor_data[index], key, 0)
    size = 1.0

    if index - 2 > 0:
        mean += get_value(raw_sensor_data[index - 2], key, 0)
        size += 1
    if index - 1 > 0:
        mean += get_value(raw_sensor_data[index - 1], key, 0)
        size += 1
    if index + 1 < len(raw_sensor_data):
        mean += get_value(raw_sensor_data[index + 1], key, 0)
        size += 1
    if index + 2 < len(raw_sensor_data):
        mean += get_value(raw_sensor_data[index + 2], key, 0)
        size += 1

    mean = int(mean / size)
    raw_sensor_data[index][key] = mean


def get_value(sensor, key, default):
    if sensor[key] is None:
        return default
    else:
        return sensor[key]


epoch = datetime.utcfromtimestamp(0)


def get_epoch_milliseconds(dt):
    return long((dt - epoch).total_seconds() * 1000.0)


def get_datetime(epoch_milliseconds):
    return datetime.utcfromtimestamp(long(epoch_milliseconds / 1000))


class RdsServer:

    def __init__(self):
        pass

    def get_data(self, crane_id, minimun_epoch, limit):

        """
        This method will return limit amount of cycles with their corresponding sensor data
        """

        a = datetime.now()

        cnx = mysql.connector.connect(user='dev_test', password='dev_test',
                                      host='vnprod.c4a62i7b81an.us-east-2.rds.amazonaws.com',
                                      database='vn')
        cursor = cnx.cursor()

        b = datetime.now()
        c = b - a
        print("Time to connect: {}".format(int(c.total_seconds() * 1000)))

        cycles = self.get_cycles(cursor, crane_id, minimun_epoch, limit)
        sensor_data = []
        if len(cycles) > 0:
            minimun_epoch = cycles[0]['step_start_time']
            maximum_epoch = cycles[-1]['step_end_time']
            sensor_data = self.get_sensor_data(cursor, crane_id, minimun_epoch, maximum_epoch, limit)

        cursor.close()
        cnx.close()
        return {'sensor': sensor_data, 'cycles': cycles}

    def get_sensor_data(self, cursor, crane_id, minimum_epoch, maximun_epoch, limit):
        view = 'view_hawkeye_rawdata_ex'
        time_column = "event_timestamp"
        return post_process_sensor_data(
            self.get_entries_since_time(cursor, crane_id, limit, minimum_epoch, time_column, view,
                                        end_time=maximun_epoch))

    def get_cycles(self, cursor, crane_id, minimum_epoch, limit):
        view = 'view_cycles_loadtype_ex'
        time_column = "step_start_time"
        return self.get_entries_since_time(cursor, crane_id, limit, minimum_epoch, time_column, view)

    @staticmethod
    def get_entries_since_time(cursor, crane_id, limit, minimum_epoch, time_column, view,
                               end_time=None):
        minimum_datetime = get_datetime(minimum_epoch)
        # 2019-01-02 16:39:04
        minimum_datetime = minimum_datetime.strftime("%Y-%m-%d %H:%M:%S")

        if end_time is None:
            cursor.execute(
                "SELECT * FROM {} WHERE {} >= '{}' AND crane_id = {} LIMIT {}".format(view, time_column,
                                                                                      minimum_datetime,
                                                                                      crane_id, limit))
        else:
            maximum_datetime = get_datetime(end_time)
            # 2019-01-02 16:39:04
            maximum_datetime = maximum_datetime.strftime("%Y-%m-%d %H:%M:%S")
            cursor.execute(
                "SELECT * FROM {} WHERE {} >= '{}' AND {} <= '{}' AND crane_id = {}".format(view, time_column,
                                                                                            minimum_datetime,
                                                                                            time_column,
                                                                                            maximum_datetime,
                                                                                            crane_id))

        row = cursor.fetchone()
        steps = []
        row_headers = [x[0] for x in cursor.description]  # this will extract row headers
        while row is not None:
            step = dict(zip(row_headers, row))
            for key, value in step.iteritems():
                if isinstance(value, datetime):
                    step[key] = get_epoch_milliseconds(value)

            steps.append(step)
            row = cursor.fetchone()
        print("Gettings {} {}".format(view, len(steps)))
        return steps

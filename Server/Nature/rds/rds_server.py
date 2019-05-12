from datetime import datetime
import time

import mysql.connector


class RdsServer:

    def get_sensor_data(self):
        cnx = mysql.connector.connect(user='dev_test', password='dev_test',
                                      host='vnprod.c4a62i7b81an.us-east-2.rds.amazonaws.com',
                                      database='vn')
        mycursor = cnx.cursor()
        mycursor.execute("SELECT * FROM view_hawkeye_rawdata_ex LIMIT 10")
        print("Columns: " + str(mycursor.column_names))
        # row1 = mycursor.fetchone()
        # while row1 is not None:
        #     print(row1)
        #    row1 = mycursor.fetchone()
        cnx.close()

    def get_steps(self, minimum_epoch, limit):
        minimum_datetime = datetime.fromtimestamp(minimum_epoch)
        cnx = mysql.connector.connect(user='dev_test', password='dev_test',
                                      host='vnprod.c4a62i7b81an.us-east-2.rds.amazonaws.com',
                                      database='vn')
        mycursor = cnx.cursor()
        mycursor.execute(
            "SELECT * FROM view_cycles_loadtype_ex WHERE step_end_time > {} LIMIT {}".format(minimum_epoch, limit))
        print("Columns: " + str(mycursor.column_names))
        row = mycursor.fetchone()

        steps = []
        row_headers = [x[0] for x in mycursor.description]  # this will extract row headers
        while row is not None:
            print(row)
            step = dict(zip(row_headers, row))
            for key, value in step.iteritems():
                if isinstance(value, datetime):
                    step[key] = int(time.mktime(value.timetuple()))

            print(str(step))
            steps.append(step)
            row = mycursor.fetchone()

        cnx.close()

        print("Gettings {} steps".format(len(steps)))
        return steps

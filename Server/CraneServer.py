from flask import Flask
from flask_restful import Api, Resource

from rds.rds_server import RdsServer

server = RdsServer()

app = Flask(__name__)
api = Api(app)


class Data(Resource):
    def get(self, craneid, amount, minmum_step):
        return server.get_data(craneid, minmum_step, amount), 200


api.add_resource(Data, "/data/<int:craneid>/<int:amount>/<int:minmum_step>")

app.run(host='0.0.0.0')
# app.run(ssl_context="adhoc", debug=True)

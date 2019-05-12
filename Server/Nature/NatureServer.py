from flask import Flask
from flask_restful import Api, Resource, reqparse

from rds.rds_server import RdsServer

server = RdsServer()

app = Flask(__name__)
api = Api(app)


class Steps(Resource):
    def get(self, amount, minmum_step):

        return server.get_steps(minmum_step, amount), 200

    def post(self, name):
        parser = reqparse.RequestParser()
        parser.add_argument("age")
        parser.add_argument("occupation")
        args = parser.parse_args()

        for user in users:
            if (name == user["name"]):
                return "User with name {} already exists".format(name), 400

        user = {
            "name": name,
            "age": args["age"],
            "occupation": args["occupation"]
        }
        users.append(user)
        return user, 201

    def put(self, name):
        parser = reqparse.RequestParser()
        parser.add_argument("age")
        parser.add_argument("occupation")
        args = parser.parse_args()

        for user in users:
            if (name == user["name"]):
                user["age"] = args["age"]
                user["occupation"] = args["occupation"]
                return user, 200

        user = {
            "name": name,
            "age": args["age"],
            "occupation": args["occupation"]
        }
        users.append(user)
        return user, 201

    def delete(self, name):
        global users
        users = [user for user in users if user["name"] != name]
        return "{} is deleted.".format(name), 200


api.add_resource(Steps, "/steps/<int:amount>/<int:minmum_step>")

app.run(host= '0.0.0.0')
#app.run(ssl_context="adhoc", debug=True)


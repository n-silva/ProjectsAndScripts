from flask import request, jsonify, Blueprint, make_response
from . import filter_by_id, filter_by_name, delete_func, update_func, create_func, validFormat

admin_blueprint = Blueprint('admin', __name__)

@admin_blueprint.route("/create", methods=['POST'])
def create():
    if request.get_json():
        if not validFormat(request.get_json().get("name")): return make_response(jsonify({"message":"Function Name format invalid! Only use: [a-zA-Z0-9_] "})), 404
        if filter_by_name(request.get_json().get("name")): return make_response(jsonify({"message": "Function name already exist"})), 404
        return make_response(jsonify(create_func(name=request.get_json().get("name"), description=request.get_json().get("description"), function=request.get_json()['function']))), 200
    return make_response(jsonify({"message":"JSON object empty"})), 404

@admin_blueprint.route("/update")
@admin_blueprint.route("/update/<id>", methods=['PUT'])
def update(id):
    get_func = filter_by_id(id)
    if not get_func: return make_response(jsonify({"message":"Function ID invalid"})), 404
    if request.get_json():
        if not validFormat(request.get_json().get("name")): return make_response(jsonify({"message":"Function Name format invalid! Only use: [a-zA-Z0-9_] "})), 404
        if (get_func["name"] != request.get_json().get("name")) and filter_by_name(request.get_json().get("name")): return make_response(jsonify({"message": "Function name already exist"})), 404
        return make_response(jsonify(update_func(id=id, name=request.get_json().get("name"), description=request.get_json().get("description"), function=request.get_json()['function']))), 200
    return make_response(jsonify({"message":"JSON object empty"})), 404


@admin_blueprint.route("/delete")
@admin_blueprint.route("/delete/<id>", methods=['DELETE'])
def delete(id):
    return make_response(jsonify({"message":delete_func(id)})), 200
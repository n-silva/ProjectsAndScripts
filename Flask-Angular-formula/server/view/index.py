from flask import Flask, request, jsonify, url_for, redirect, Blueprint, make_response
from . import filter_by_id, filter_by_name, filter_all, FuncCalculate

index_blueprint = Blueprint('index', __name__)

@index_blueprint.route('/', methods=['GET'])
def index():
    if request.args.get("calc"): return make_response(
        jsonify(FuncCalculate().eval_expression(input_string=request.args.get("calc")))), 200
    return make_response(jsonify({'formula': 'Enter Mathematical Functions', 'result': 'Example: f1 + f2 + 5'})), 200

@index_blueprint.route('/search', methods=['GET'])
def search():
    if request.args.get("id"): return make_response(jsonify(filter_by_id(request.args.get("id")))), 200
    if request.args.get("name"): return make_response(jsonify(filter_by_name(request.args.get("name")))), 200
    return make_response(jsonify(filter_all())), 200


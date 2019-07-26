from django.shortcuts import render, get_object_or_404
from django.http import HttpResponse, Http404
from django.template import loader
from .models import Team

def index(request):
    team_list = Team.objects.order_by('name')[:5]
    template = loader.get_template('team/index.html')
    context = {
        'team_list': team_list,
    }
    return HttpResponse(template.render(context, request))

def detail(request, team_id):
    try:
        team = get_object_or_404(Team, pk=team_id)
    except Team.DoesNotExist:
        raise Http404("Question does not exist")
    return render(request, 'team/details.html', {'team': team})


def results(request, team_id):
    response = "You're looking at the results of team %s."
    return HttpResponse(response % team_id)

def players(request, team_id):
    return HttpResponse("Are players on team %s." % team_id)
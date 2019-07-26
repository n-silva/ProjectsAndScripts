from django.urls import path

from . import views

urlpatterns = [
    path('', views.index, name='index'),
    path('team/<int:team_id>/', views.detail, name='detail'),
    path('team/<int:team_id>/results/', views.results, name='results'),
    path('team/<int:team_id>/players/', views.players, name='players'),
]
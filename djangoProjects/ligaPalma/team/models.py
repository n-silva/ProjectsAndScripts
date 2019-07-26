from django.db import models

class Team(models.Model):
    name = models.CharField(max_length=200)

    def __str__(self):
        return self.name

class Player(models.Model):
    team = models.ForeignKey(Team, on_delete=models.CASCADE)
    name = models.CharField(max_length=200)
    age = models.IntegerField(default=0)
    avatar = models.TextField()

    def __str__(self):
        return self.name

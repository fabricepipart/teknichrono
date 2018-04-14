#!python3

from base import *

categoryUrl = '/rest/categories'

# ----------------------------------------------------------------------


def addCategory(name):
  "This adds a Category"
  data = '{"name":"' + name + '"}'
  post(data, categoryUrl)
  print("Category " + name + " added")
  categoryResponse = getCategoryByName(name)
  return categoryResponse


def getCategoryByName(name):
  "This gets a Category by name and returns a json"
  url = categoryUrl + '/name'
  params = {'name': name}
  categoryResponse = get(url, params)
  return categoryResponse


def deleteCategory(id):
  "This deletes a Category by id"
  url = categoryUrl + '/' + str(id)
  delete(url)
  print("Deleted Category id " + str(id))
  return


def getCategories():
  "This gets all Categories"
  categoryResponse = get(categoryUrl)
  return categoryResponse


def deleteCategories():
  "Deletes all Categories"
  categories = getCategories()
  for category in categories:
    deleteCategory(category['id'])
  return


def addPilotToCategory(categoryId, pilotId):
  "Associate Category and Pilot"
  url = categoryUrl + '/' + str(categoryId) + '/addPilot?pilotId=' + str(pilotId)
  post('', url)
  print("Associate Category id " + str(categoryId) + " and pilot id " + str(pilotId))
  return


# ----------------------------------------------------------------------

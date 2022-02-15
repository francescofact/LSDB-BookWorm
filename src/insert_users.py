import random
import pymongo

server = pymongo.MongoClient("mongodb://localhost:27017/")
mydb = server["db"]
mycol = mydb["Users"]
mybooks = mydb["Books"]


print()


f = open('users.csv')
f2 = open('common_passwords.csv')
f.readline()
f2.readline()
for i in range(1,200):
    username = f.readline().split(",")[0]
    password = f2.readline().split(",")[0]
    print((username, password))
    #random reviews
    ratings = []
    for i in range (1,15):
        book = mybooks.aggregate([{"$sample": {"size": 1}}]).next()
        ratings.append({"book": book["title"], "value": random.randrange(1,5)})
    user = { "username": username, "password": password, "type": "basic", "Ratings":ratings }
    x = mycol.insert_one(user)

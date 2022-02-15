import random
import pymongo

server = pymongo.MongoClient("mongodb://localhost:27017/")
mydb = server["db"]
mycol = mydb["Users"]


f = open('users.csv')
f2 = open('common_passwords.csv')
f.readline()
f2.readline()
for i in range(1,200):
    username = f.readline().split(",")[0]
    password = f2.readline().split(",")[0]
    print((username, password))
    user = { "username": username, "password": password, "type": "basic", "Ratings":[] }
    x = mycol.insert_one(user)
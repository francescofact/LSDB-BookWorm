import random
import pymongo
from neo4j import GraphDatabase

server = pymongo.MongoClient("mongodb://localhost:27017/")
mydb = server["db"]
mycol = mydb["Users"]
mybooks = mydb["Books"]

class Neo4jConnection:

    def __init__(self, uri, user, pwd):
        self.__uri = uri
        self.__user = user
        self.__pwd = pwd
        self.__driver = None
        try:
            self.__driver = GraphDatabase.driver(self.__uri, auth=(self.__user, self.__pwd))
        except Exception as e:
            print("Failed to create the driver:", e)

    def close(self):
        if self.__driver is not None:
            self.__driver.close()

    def query(self, query, db=None, title=None):
        assert self.__driver is not None, "Driver not initialized!"
        session = None
        response = None
        try:
            session = self.__driver.session(database=db) if db is not None else self.__driver.session()
            response = ""
            if title is None:
                response = list(session.run(query))
            else:
                response = list(session.run(query, title=title))
        except Exception as e:
            print("Query failed:", e)
        finally:
            if session is not None:
                session.close()
        return response

conn = Neo4jConnection(uri="bolt://localhost:7687", user="neo4j", pwd="123")

f = open('users.csv')
f2 = open('common_passwords.csv')
f.readline()
f2.readline()

books = []
usernames = []
for i in range(1,200):
    username = f.readline().split(",")[0]
    password = f2.readline().split(",")[0]

    conn.query("CREATE (a:Person{name:'"+username+"'})", db="neo4j")
    #random reviews
    ratings = []
    for i in range (1,15):
        book = mybooks.aggregate([{"$sample": {"size": 1}}]).next()
        val = float(random.randrange(1,5))

        if (book["title"] not in books):
            conn.query("MERGE (n:Book {name: $title})", title=book["title"])
            books.append(book["title"])

        conn.query("MATCH (p:Person) "
            + "WHERE p.name = '"+username+"' "
            + "MATCH (b:Book) "
            + "WHERE b.name = $title "
            + "MERGE (p)-[:RATED {rating: "+str(val)+"}]->(b)", title=book["title"])

        ratings.append({"book": book["title"], "value": val})
    user = { "username": username, "password": password, "type": "basic", "Ratings":ratings }
    x = mycol.insert_one(user)

    #followers
    picked = []
    if (len(usernames)>5):
        for i in range(1,4):
            if (i not in picked):
                tofollow = usernames[i]
                conn.query("MATCH (p:Person) "
                                + "WHERE p.name = '"+username+"' "
                                + "MATCH (n:Person) "
                                + "WHERE n.name = '"+tofollow+"' "
                                + "MERGE (p)-[:FOLLOW]->(n)")
                picked.append(i)
    usernames.append(username)
    print((username, password))


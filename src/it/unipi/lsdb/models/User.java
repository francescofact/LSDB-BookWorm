package it.unipi.giar.Data;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

import javax.xml.bind.DatatypeConverter;

import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.TransactionWork;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import it.unipi.giar.GiarSession;
import it.unipi.giar.MongoDriver;
import it.unipi.giar.Neo4jDriver;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Accumulators.sum;
import static com.mongodb.client.model.Aggregates.group;
import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Aggregates.unwind;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Aggregates.limit;
import static com.mongodb.client.model.Aggregates.sort;
import static com.mongodb.client.model.Sorts.descending;

import static org.neo4j.driver.v1.Values.parameters;
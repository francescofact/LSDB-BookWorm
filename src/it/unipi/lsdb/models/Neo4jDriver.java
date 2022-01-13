import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;

import static org.neo4j.driver.Values.parameters;

public class Neo4jDriver {
    private static Neo4jDriver neo = null;
    private final Driver driver;

    Neo4jDriver() {
        driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "123"));
    }

    public static Neo4jDriver getInstance() {
        if(neo == null)
            neo = new Neo4jDriver();

        return neo;
    }

    public Driver getDriver() {
        if(neo == null)
            throw new RuntimeException("Connection doesn't exist.");
        else
            return neo.driver;
    }

    public void close() {
        if(neo == null)
            throw new RuntimeException("Connection doesn't exist.");
        else
            neo.driver.close();
    }

}
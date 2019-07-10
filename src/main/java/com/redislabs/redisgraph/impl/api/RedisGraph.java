package com.redislabs.redisgraph.impl.api;

import com.redislabs.redisgraph.RedisGraphContexted;
import com.redislabs.redisgraph.RedisGraphGeneralContext;
import com.redislabs.redisgraph.ResultSet;
import com.redislabs.redisgraph.impl.graph_cache.RedisGraphCaches;
import com.redislabs.redisgraph.impl.resultset.ResultSetImpl;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.util.Pool;
import redis.clients.jedis.util.SafeEncoder;

import java.util.*;



/**
 *
 */
public class RedisGraph extends AbstractRedisGraph implements RedisGraphGeneralContext {

    private final Pool<Jedis> client;
    private RedisGraphCaches caches = new RedisGraphCaches();

    /**
     * Creates a client running on the local machine

     */
    public RedisGraph() {
        this("localhost", 6379);
    }

    /**
     * Creates a client running on the specific host/post
     *
     * @param host Redis host
     * @param port Redis port
     */
    public RedisGraph(String host, int port) {
        this( new JedisPool(host, port));
    }

    /**
     * Creates a client using provided Jedis pool
     *
     * @param jedis bring your own Jedis pool
     */
    public RedisGraph( Pool<Jedis> jedis) {
        this.client = jedis;
    }


    /**
     * Overrides the abstract function. Gets and returns a Jedis connection from the Jedis pool
     * @return a Jedis connection
     */
    @Override
    protected Jedis getConnection() {
        return client.getResource();
    }

    /**
     * Overrides the abstract function.
     * Sends the query from any Jedis connection received from the Jedis pool and closes it once done
     * @param graphId graph to be queried
     * @param preparedQuery prepared query
     * @return Result set with the query answer
     */
    @Override
    protected ResultSet sendQuery(String graphId, String preparedQuery){
        List<Object> rawResponse;
        try(Jedis conn = getConnection()){
            rawResponse = (List<Object>) conn.sendCommand(RedisGraphCommand.QUERY, graphId, preparedQuery, "--COMPACT");
        }
        return new ResultSetImpl(rawResponse, this, graphId, caches.getGraphCache(graphId));
    }


    /**
     * Closes the Jedis pool
     */
    @Override
    public void close(){
        this.client.close();
    }


    /**
     * Deletes the entire graph
     * @param graphId graph to delete
     * @return delete running time statistics
     */
    @Override
    public String deleteGraph(String graphId) {
        try (Jedis conn = getConnection()) {
            Object response = conn.sendCommand(RedisGraphCommand.DELETE, graphId);
            //clear local state
            caches.removeGraphCache(graphId);
            return SafeEncoder.encode((byte[]) response);
        }
    }

    /**
     * Returns a new ContextedRedisGraph bounded to a Jedis connection from the Jedis pool
     * @return ContextedRedisGraph
     */
    @Override
    public RedisGraphContexted getContextedAPI() {
        ContextedRedisGraph contextedRedisGraph =  new ContextedRedisGraph(getConnection());
        contextedRedisGraph.setRedisGraphCaches(this.caches);
        return contextedRedisGraph;
    }
}

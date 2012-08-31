package com.thinkaurelius.faunus.mapreduce.transform;

import com.thinkaurelius.faunus.FaunusConfiguration;
import com.thinkaurelius.faunus.FaunusEdge;
import com.thinkaurelius.faunus.FaunusVertex;
import com.thinkaurelius.faunus.Tokens;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class EdgesVerticesMap {

    public static final String DIRECTION = Tokens.makeNamespace(VerticesEdgesMapReduce.class) + ".direction";

    public enum Counters {
        EDGES_PROCESSED
    }

    public static class Map extends Mapper<NullWritable, FaunusVertex, NullWritable, FaunusVertex> {

        private Direction direction;

        @Override
        public void setup(final Mapper.Context context) throws IOException, InterruptedException {
            this.direction = Direction.valueOf(context.getConfiguration().get(DIRECTION));
        }

        @Override
        public void map(final NullWritable key, final FaunusVertex value, final Mapper<NullWritable, FaunusVertex, NullWritable, FaunusVertex>.Context context) throws IOException, InterruptedException {
            long edgesProcessed = 0;
            for (final Edge e : value.getEdges(this.direction)) {
                final FaunusEdge edge = (FaunusEdge) e;
                if (edge.hasPaths()) {
                    value.addPaths(edge.getPaths(), true);
                    edgesProcessed++;
                }
            }

            context.write(NullWritable.get(), value);
            context.getCounter(Counters.EDGES_PROCESSED).increment(edgesProcessed);
        }
    }

}

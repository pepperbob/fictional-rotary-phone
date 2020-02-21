package de.byoc.gobbler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.byoc.events.RawEvent;
import io.reactivex.Flowable;
import io.reactivex.Maybe;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class GobbleRule {

    private final List<GobbleFn> gobbleFn;

    public GobbleRule(GobbleFn... fns) {
        this(Arrays.asList(fns));
    }

    public GobbleRule(List<GobbleFn> fns) {
        this.gobbleFn = fns;
    }

    public Maybe<RawEvent> gobble(ObjectMapper objectMapper, RawEvent event) {
        return Flowable.fromIterable(gobbleFn)
                .filter(x -> x.test.test(event))
                .reduce(GobbleFn::andThen)
                .map(x -> x.gobble(objectMapper, event));
    }

    static class GobbleFn {

        private final Predicate<RawEvent> test;
        private final Function<ObjectNode, ObjectNode> fn;

        public GobbleFn(Predicate<RawEvent> test, Function<ObjectNode, ObjectNode> fn) {
            this.test = test;
            this.fn = fn;
        }

        public RawEvent gobble(ObjectMapper objectMapper, RawEvent eventToGobble) {
            try {
                return eventToGobble.withPayload(objectMapper
                        .writeValueAsBytes(fn.apply((ObjectNode) objectMapper.readTree(eventToGobble.payload()))));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public GobbleFn andThen(GobbleFn b) {
            return new GobbleFn(this.test.and(b.test), this.fn.andThen(b.fn));
        }

        public static GobbleFn gobbel(Predicate<RawEvent> test, Function<ObjectNode, ObjectNode> fn) {
            return new GobbleRule.GobbleFn(test, fn);
        }

        public static GobbleFn gobbel(Class<?> clazz, Function<ObjectNode, ObjectNode> fn) {
            return gobbel(e -> e.payloadType().equals(clazz.getName()), fn);
        }

    }

}

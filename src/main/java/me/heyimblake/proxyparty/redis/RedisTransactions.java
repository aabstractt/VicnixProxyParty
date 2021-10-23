package me.heyimblake.proxyparty.redis;

import com.imaginarycode.minecraft.redisbungee.internal.jedis.Jedis;
import com.imaginarycode.minecraft.redisbungee.internal.jedis.JedisPool;
import lombok.AllArgsConstructor;

import java.util.function.Consumer;
import java.util.function.Function;

@AllArgsConstructor
public class RedisTransactions {

    private JedisPool jedisPool;
    private String password;

    public <T> T runTransaction(Function<Jedis, T> action) {
        try (Jedis jedis = jedisPool.getResource()) {
            if (password != null) {
                jedis.auth(password);
            }

            return action.apply(jedis);
        }
    }

    public void runTransaction(Consumer<Jedis> action) {
        try (Jedis jedis = jedisPool.getResource()) {
            if (password != null) {
                jedis.auth(password);
            }

            action.accept(jedis);
        }
    }
}
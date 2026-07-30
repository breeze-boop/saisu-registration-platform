package com.mishi.seckill;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

class SeckillLuaScriptTest {

    @Test
    void scriptContainsAtomicStockAndOneOrderGuards() throws IOException {
        String script = new String(
                new ClassPathResource("lua/seckill.lua").getInputStream().readAllBytes(),
                StandardCharsets.UTF_8);

        assertThat(script).contains("stockKey");
        assertThat(script).contains("orderKey");
        assertThat(script).contains("redis.call('GET', stockKey)");
        assertThat(script).contains("redis.call('SISMEMBER', orderKey, userId)");
        assertThat(script).contains("redis.call('INCRBY', stockKey, -1)");
        assertThat(script).contains("redis.call('SADD', orderKey, userId)");
    }
}


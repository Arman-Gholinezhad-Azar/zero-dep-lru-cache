/*
   Copyright 2026 Arman Gholinezhad Azar

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

*/
package com.particelagency.cache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CacheStorageTest {

    private CacheStorage<String, String> cache;

    @Nested
    @DisplayName("Constructor Validation")
    class ConstructorTests {
        @Test
        void should_throwException_when_capacityIsZeroOrNegative() {
            assertThatThrownBy(() -> new CacheStorage<String, String>(0))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Capacity must be greater than zero");

            assertThatThrownBy(() -> new CacheStorage<String, String>(-5))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("Basic Operations & Contracts")
    class BasicOperations {
        @BeforeEach
        void setUp() {
            cache = new CacheStorage<>(3);
        }

        @Test
        void should_returnNull_when_keyDoesNotExist() {
            assertThat(cache.get("missing_key")).isNull();
        }

        @Test
        void should_throwNullPointerException_when_keyIsNull() {
            assertThatThrownBy(() -> cache.get(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("Cache key cannot be null");

            assertThatThrownBy(() -> cache.put(null, "value"))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        void should_storeAndRetrieveValueSuccessfully() {
            cache.put("user:1", "Arman");
            assertThat(cache.get("user:1")).isEqualTo("Arman");
            assertThat(cache.getSize()).isEqualTo(1);
        }

        @Test
        void should_updateExistingKey_withoutIncreasingSize() {
            cache.put("user:1", "Arman");
            cache.put("user:1", "Arman_Updated");

            assertThat(cache.get("user:1")).isEqualTo("Arman_Updated");
            assertThat(cache.getSize()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("LRU Eviction Invariant Tests")
    class EvictionTests {
        @BeforeEach
        void setUp() {
            cache = new CacheStorage<>(3);
        }

        @Test
        void should_evictOldestEntry_whenCapacityExceeded() {
            cache.put("k1", "v1");
            cache.put("k2", "v2");
            cache.put("k3", "v3");

            cache.put("k4", "v4");

            assertThat(cache.get("k1")).isNull(); // Evicted
            assertThat(cache.get("k2")).isEqualTo("v2");
            assertThat(cache.get("k3")).isEqualTo("v3");
            assertThat(cache.get("k4")).isEqualTo("v4");
            assertThat(cache.getSize()).isEqualTo(3);
        }

        @Test
        void should_refreshRecencyOnGet_preventingItFromEviction() {
            cache.put("k1", "v1");
            cache.put("k2", "v2");
            cache.put("k3", "v3");

            cache.get("k1");

            cache.put("k4", "v4");

            assertThat(cache.get("k1")).isEqualTo("v1");
            assertThat(cache.get("k2")).isNull();  
            assertThat(cache.get("k3")).isEqualTo("v3");
            assertThat(cache.get("k4")).isEqualTo("v4");
        }

        @Test
        void should_refreshRecencyOnPutOverwrite_preventingItFromEviction() {
            cache.put("k1", "v1");
            cache.put("k2", "v2");
            cache.put("k3", "v3");

            cache.put("k1", "v1_updated");

            cache.put("k4", "v4");

            assertThat(cache.get("k1")).isEqualTo("v1_updated");
            assertThat(cache.get("k2")).isNull();
            assertThat(cache.get("k3")).isEqualTo("v3");
            assertThat(cache.get("k4")).isEqualTo("v4");
        }
    }

    @Nested
    @DisplayName("Purge & Removal")
    class RemovalTests {
        @Test
        void should_resetStateCleanly_onClear() {
            CacheStorage<String, String> cache = new CacheStorage<>(2);
            cache.put("k1", "v1");
            cache.put("k2", "v2");

            cache.clear();

            assertThat(cache.getSize()).isZero();
            assertThat(cache.get("k1")).isNull();
            assertThat(cache.get("k2")).isNull();

            cache.put("k3", "v3");
            assertThat(cache.get("k3")).isEqualTo("v3");
            assertThat(cache.getSize()).isEqualTo(1);
        }
    }
}

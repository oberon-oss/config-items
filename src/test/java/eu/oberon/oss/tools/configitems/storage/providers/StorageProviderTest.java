package eu.oberon.oss.tools.configitems.storage.providers;

import eu.oberon.oss.tools.configitems.storage.StorableConfigurationItem;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StorageProviderTest {

    @Test
    void defaultFlushThrowsUnsupportedOperationException() {
        StorageProvider provider = new StorageProvider() {
            @Override
            public <I, K, A, P> void storeConfigurationItem(StorableConfigurationItem<I, K, A, P> item) {
                // Testing only
            }

            @Override
            public <I, K, A, P> void loadConfigurationItem(StorableConfigurationItem<I, K, A, P> item) {
                // Testing only
            }

            @Override
            public boolean isKeyClassTypeAllowed(Class<?> keyClass) {
                return false;
            }

            @Override
            public boolean isStorageClassTypeAllowed(Class<?> valueClass) {
                return false;
            }
        };

        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class, provider::flush);
        assertEquals("Flushing is not supported by this storage provider implementation.", exception.getMessage());
    }
}

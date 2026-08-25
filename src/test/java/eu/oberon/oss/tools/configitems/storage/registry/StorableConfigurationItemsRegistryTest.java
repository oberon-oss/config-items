package eu.oberon.oss.tools.configitems.storage.registry;

import eu.oberon.oss.tools.configitems.storage.ConfigurationItemKey;
import eu.oberon.oss.tools.configitems.storage.RegisteredConfigurationItem;
import eu.oberon.oss.tools.configitems.storage.StorableConfigurationItem;
import eu.oberon.oss.tools.configitems.storage.providers.StorageProvider;
import eu.oberon.oss.tools.configitems.storage.registry.listeners.ConfigurationItemRegistryEvent;
import eu.oberon.oss.tools.configitems.storage.registry.listeners.ConfigurationItemRegistryEventType;
import eu.oberon.oss.tools.configitems.storage.registry.listeners.ConfigurationItemRegistryListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class StorableConfigurationItemsRegistryTest {

    private DefaultStorableConfigurationItemsRegistry registry;
    private TestStorageProvider storageProvider;

    @BeforeEach
    void setUp() {
        registry = new DefaultStorableConfigurationItemsRegistry();
        storageProvider = new TestStorageProvider();
    }

    @Test
    void registerStoresItemByItsKey() {
        TestStorableConfigurationItem<String> item = createStringItem("test-key", "value");

        registry.register(item);

        assertSame(item, registry.getItem("test-key"));
    }

    @Test
    void registerRejectsNullItem() {
        assertThrows(NullPointerException.class, () -> registry.register(null));
    }

    @Test
    void registerRejectsItemWithNullKey() {
        TestStorableConfigurationItem<String> item = createStringItem(null, "value");

        assertThrows(NullPointerException.class, () -> registry.register(item));
    }

    @Test
    void registerRejectsDuplicateKey() {
        TestStorableConfigurationItem<String> firstItem = createStringItem("test-key", "first");
        TestStorableConfigurationItem<String> secondItem = createStringItem("test-key", "second");

        registry.register(firstItem);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> registry.register(secondItem));

        assertEquals("A configuration item with key 'test-key' is already registered", exception.getMessage());
        assertSame(firstItem, registry.getItem("test-key"));
    }

    @Test
    void replaceRegistersItemWhenKeyDoesNotExistYet() {
        TestStorableConfigurationItem<String> item = createStringItem("test-key", "value");

        RegisteredConfigurationItem previousItem = registry.replace(item);

        assertNull(previousItem);
        assertSame(item, registry.getItem("test-key"));
    }

    @Test
    void replaceReturnsPreviousItemAndStoresNewItem() {
        TestStorableConfigurationItem<String> firstItem = createStringItem("test-key", "first");
        TestStorableConfigurationItem<String> secondItem = createStringItem("test-key", "second");

        registry.register(firstItem);

        RegisteredConfigurationItem previousItem = registry.replace(secondItem);

        assertSame(firstItem, previousItem);
        assertSame(secondItem, registry.getItem("test-key"));
    }

    @Test
    void replaceRejectsNullItem() {
        assertThrows(NullPointerException.class, () -> registry.replace(null));
    }

    @Test
    void replaceRejectsItemWithNullKey() {
        TestStorableConfigurationItem<String> item = createStringItem(null, "value");

        assertThrows(NullPointerException.class, () -> registry.replace(item));
    }

    @Test
    void getItemWithTypedKeyReturnsRegisteredItem() {
        ConfigurationItemKey<String> key = ConfigurationItemKey.of("test-key", String.class);
        TestStorableConfigurationItem<String> item = createStringItem("test-key", "value");

        registry.register(item);

        //noinspection AssertBetweenInconvertibleTypes - delibirate testing case
        assertSame(item, registry.getItem(key));
    }

    @Test
    void getItemWithTypedKeyReturnsNullWhenItemDoesNotExist() {
        ConfigurationItemKey<String> key = ConfigurationItemKey.of("missing-key", String.class);

        assertNull(registry.getItem(key));
    }

    @Test
    void getItemWithRawIdReturnsNullWhenItemDoesNotExist() {
        assertNull(registry.getItem("missing-key"));
    }

    @Test
    void getItemRejectsNullTypedKey() {
        assertThrows(NullPointerException.class, () -> registry.getItem((ConfigurationItemKey<?>) null));
    }

    @Test
    void getItemRejectsNullRawId() {
        assertThrows(NullPointerException.class, () -> registry.getItem((Object) null));
    }

    @Test
    void findItemReturnsItemWhenPresent() {
        ConfigurationItemKey<String> key = ConfigurationItemKey.of("test-key", String.class);
        TestStorableConfigurationItem<String> item = createStringItem("test-key", "value");

        registry.register(item);

        Optional<StorableConfigurationItem<Object, String, String, String>> foundItem = registry.findItem(key);

        assertTrue(foundItem.isPresent());
        //noinspection AssertBetweenInconvertibleTypes - delibirate testing case
        assertSame(item, foundItem.get());
    }

    @Test
    void findItemReturnsEmptyWhenMissing() {
        ConfigurationItemKey<String> key = ConfigurationItemKey.of("missing-key", String.class);

        assertTrue(registry.findItem(key).isEmpty());
    }

    @Test
    void getRequiredItemReturnsItemWhenPresent() {
        ConfigurationItemKey<String> key = ConfigurationItemKey.of("test-key", String.class);
        TestStorableConfigurationItem<String> item = createStringItem("test-key", "value");

        registry.register(item);

        //noinspection AssertBetweenInconvertibleTypes - delibirate testing case
        assertSame(item, registry.getRequiredItem(key));
    }

    @Test
    void getRequiredItemThrowsWhenMissing() {
        ConfigurationItemKey<String> key = ConfigurationItemKey.of("missing-key", String.class);

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> registry.getRequiredItem(key));

        assertEquals("No configuration item registered for key 'missing-key'", exception.getMessage());
    }

    @Test
    void getCurrentValueReturnsTypedValue() {
        ConfigurationItemKey<String> key = ConfigurationItemKey.of("test-key", String.class);
        TestStorableConfigurationItem<String> item = createStringItem("test-key", "value");

        registry.register(item);

        String value = registry.getAccessor().getCurrentValue(key);

        assertEquals("value", value);
    }

    @Test
    void getCurrentValueReturnsNullWhenItemDoesNotExist() {
        ConfigurationItemKey<String> key = ConfigurationItemKey.of("missing-key", String.class);

        assertNull(registry.getAccessor().getCurrentValue(key));
    }

    @Test
    void getCurrentValueReturnsNullWhenCurrentValueIsNull() {
        ConfigurationItemKey<String> key = ConfigurationItemKey.of("test-key", String.class);
        TestStorableConfigurationItem<String> item = createStringItem("test-key", null);

        registry.register(item);

        assertNull(registry.getAccessor().getCurrentValue(key));
    }

    @Test
    void getCurrentValueThrowsWhenStoredValueDoesNotMatchTypedKeyValueType() {
        ConfigurationItemKey<Integer> key = ConfigurationItemKey.of("test-key", Integer.class);
        TestStorableConfigurationItem<String> item = createStringItem("test-key", "not-an-integer");

        registry.register(item);

        ConfigurationItemsRegistryAccessor accessor = registry.getAccessor();
        assertThrows(ClassCastException.class, () -> accessor.getCurrentValue(key));
    }

    @Test
    void findCurrentValueReturnsValueWhenPresentAndNonNull() {
        ConfigurationItemKey<String> key = ConfigurationItemKey.of("test-key", String.class);
        TestStorableConfigurationItem<String> item = createStringItem("test-key", "value");

        registry.register(item);

        Optional<String> value = registry.getAccessor().findCurrentValue(key);

        assertTrue(value.isPresent());
        assertEquals("value", value.get());
    }

    @Test
    void findCurrentValueReturnsEmptyWhenItemIsMissing() {
        ConfigurationItemKey<String> key = ConfigurationItemKey.of("missing-key", String.class);

        assertTrue(registry.getAccessor().findCurrentValue(key).isEmpty());
    }

    @Test
    void findCurrentValueReturnsEmptyWhenCurrentValueIsNull() {
        ConfigurationItemKey<String> key = ConfigurationItemKey.of("test-key", String.class);
        TestStorableConfigurationItem<String> item = createStringItem("test-key", null);

        registry.register(item);

        assertTrue(registry.getAccessor().findCurrentValue(key).isEmpty());
    }

    @Test
    void getRequiredCurrentValueReturnsValueWhenItemExists() {
        ConfigurationItemKey<Integer> key = ConfigurationItemKey.of("test-key", Integer.class);
        TestStorableConfigurationItem<Integer> item = createIntegerItem("test-key", 123);

        registry.register(item);

        Integer value = registry.getAccessor().getRequiredCurrentValue(key);

        assertEquals(123, value);
    }

    @Test
    void getRequiredCurrentValueReturnsNullWhenItemExistsAndCurrentValueIsNull() {
        ConfigurationItemKey<String> key = ConfigurationItemKey.of("test-key", String.class);
        TestStorableConfigurationItem<String> item = createStringItem("test-key", null);

        registry.register(item);

        assertNull(registry.getAccessor().getRequiredCurrentValue(key));
    }

    @Test
    void getRequiredCurrentValueThrowsWhenItemIsMissing() {
        ConfigurationItemKey<String> key = ConfigurationItemKey.of("missing-key", String.class);

        ConfigurationItemsRegistryAccessor accessor = registry.getAccessor();
        assertThrows(NoSuchElementException.class, () -> accessor.getRequiredCurrentValue(key));
    }

    @Test
    void setCurrentValueUpdatesRegisteredItem() {
        ConfigurationItemKey<String> key = ConfigurationItemKey.of("test-key", String.class);
        TestStorableConfigurationItem<String> item = createStringItem("test-key", "before");

        registry.register(item);

        boolean updated = registry.getAccessor().setCurrentValue(key, "after");

        assertTrue(updated);
        assertEquals("after", item.getCurrentValue());
    }

    @Test
    void setCurrentValueAllowsNullValue() {
        ConfigurationItemKey<String> key = ConfigurationItemKey.of("test-key", String.class);
        TestStorableConfigurationItem<String> item = createStringItem("test-key", "before");

        registry.register(item);

        boolean updated = registry.getAccessor().setCurrentValue(key, null);

        assertTrue(updated);
        assertNull(item.getCurrentValue());
    }

    @Test
    void setCurrentValueThrowsWhenValueTypeDoesNotMatchKey() {
        ConfigurationItemKey<Integer> key = ConfigurationItemKey.of("test-key", Integer.class);
        registry.register(createIntegerItem("test-key", 123));

        ConfigurationItemsRegistryAccessor accessor = registry.getAccessor();
        //noinspection unchecked,rawtypes
        assertThrows(ClassCastException.class, () -> accessor.setCurrentValue((ConfigurationItemKey) key, "not-an-integer"));
    }

    @Test
    void setCurrentValueReturnsFalseWhenItemIsMissing() {
        ConfigurationItemKey<String> key = ConfigurationItemKey.of("missing-key", String.class);

        boolean updated = registry.getAccessor().setCurrentValue(key, "value");

        assertFalse(updated);
    }

    @Test
    void setCurrentValueRejectsNullKey() {
        ConfigurationItemsRegistryAccessor accessor = registry.getAccessor();
        assertThrows(NullPointerException.class, () -> accessor.setCurrentValue(null, "value"));
    }

    @Test
    void setRequiredCurrentValueUpdatesRegisteredItem() {
        ConfigurationItemKey<String> key = ConfigurationItemKey.of("test-key", String.class);
        TestStorableConfigurationItem<String> item = createStringItem("test-key", "before");

        registry.register(item);

        registry.getAccessor().setRequiredCurrentValue(key, "after");

        assertEquals("after", item.getCurrentValue());
    }

    @Test
    void setRequiredCurrentValueAllowsNullValue() {
        ConfigurationItemKey<String> key = ConfigurationItemKey.of("test-key", String.class);
        TestStorableConfigurationItem<String> item = createStringItem("test-key", "before");

        registry.register(item);

        registry.getAccessor().setRequiredCurrentValue(key, null);

        assertNull(item.getCurrentValue());
    }

    @Test
    void setRequiredCurrentValueThrowsWhenValueTypeDoesNotMatchKey() {
        ConfigurationItemKey<Integer> key = ConfigurationItemKey.of("test-key", Integer.class);
        registry.register(createIntegerItem("test-key", 123));

        ConfigurationItemsRegistryAccessor accessor = registry.getAccessor();
        //noinspection unchecked,rawtypes
        assertThrows(ClassCastException.class, () -> accessor.setRequiredCurrentValue((ConfigurationItemKey) key, "not-an-integer"));
    }

    @Test
    void setRequiredCurrentValueThrowsWhenItemIsMissing() {
        ConfigurationItemKey<String> key = ConfigurationItemKey.of("missing-key", String.class);

        ConfigurationItemsRegistryAccessor accessor = registry.getAccessor();
        assertThrows(NoSuchElementException.class, () -> accessor.setRequiredCurrentValue(key, "value"));
    }

    @Test
    void containsItemReturnsTrueWhenTypedKeyExists() {
        ConfigurationItemKey<String> key = ConfigurationItemKey.of("test-key", String.class);
        registry.register(createStringItem("test-key", "value"));

        assertTrue(registry.containsItem(key));
    }

    @Test
    void containsItemReturnsFalseWhenTypedKeyDoesNotExist() {
        ConfigurationItemKey<String> key = ConfigurationItemKey.of("missing-key", String.class);

        assertFalse(registry.containsItem(key));
    }

    @Test
    void containsItemRejectsNullTypedKey() {
        assertThrows(NullPointerException.class, () -> registry.containsItem(null));
    }

    @Test
    void containsItemUsesKeyIdOnly() {
        ConfigurationItemKey<Integer> integerKey = ConfigurationItemKey.of("test-key", Integer.class);
        registry.register(createStringItem("test-key", "value"));

        assertTrue(registry.containsItem(integerKey));
    }

    @Test
    void containsItemIdReturnsTrueWhenRawIdExists() {
        registry.register(createStringItem("test-key", "value"));

        assertTrue(registry.containsItemId("test-key"));
    }

    @Test
    void containsItemIdReturnsFalseWhenRawIdDoesNotExist() {
        assertFalse(registry.containsItemId("missing-key"));
    }

    @Test
    void containsItemIdRejectsNullRawId() {
        assertThrows(NullPointerException.class, () -> registry.containsItemId(null));
    }

    @Test
    void unregisterWithTypedKeyRemovesItem() {
        ConfigurationItemKey<String> key = ConfigurationItemKey.of("test-key", String.class);
        TestStorableConfigurationItem<String> item = createStringItem("test-key", "value");

        registry.register(item);

        RegisteredConfigurationItem removedItem = registry.unregister(key);

        assertSame(item, removedItem);
        assertFalse(registry.containsItem(key));
        assertEquals(0, registry.size());
    }

    @Test
    void unregisterWithTypedKeyReturnsNullWhenItemDoesNotExist() {
        ConfigurationItemKey<String> key = ConfigurationItemKey.of("missing-key", String.class);

        assertNull(registry.unregister(key));
    }

    @Test
    void unregisterRejectsNullTypedKey() {
        assertThrows(NullPointerException.class, () -> registry.unregister(null));
    }

    @Test
    void unregisterByIdRemovesItem() {
        TestStorableConfigurationItem<String> item = createStringItem("test-key", "value");

        registry.register(item);

        RegisteredConfigurationItem removedItem = registry.unregisterById("test-key");

        assertSame(item, removedItem);
        assertFalse(registry.containsItemId("test-key"));
        assertEquals(0, registry.size());
    }

    @Test
    void unregisterByIdReturnsNullWhenItemDoesNotExist() {
        assertNull(registry.unregisterById("missing-key"));
    }

    @Test
    void unregisterByIdRejectsNullRawId() {
        assertThrows(NullPointerException.class, () -> registry.unregisterById(null));
    }

    @Test
    void clearRemovesAllItems() {
        registry.register(createStringItem("first-key", "first"));
        registry.register(createIntegerItem("second-key", 123));

        registry.clear();

        assertEquals(0, registry.size());
        assertFalse(registry.containsItemId("first-key"));
        assertFalse(registry.containsItemId("second-key"));
    }

    @Test
    void sizeReturnsNumberOfRegisteredItems() {
        assertEquals(0, registry.size());

        registry.register(createStringItem("first-key", "first"));
        registry.register(createIntegerItem("second-key", 123));

        assertEquals(2, registry.size());
    }

    @Test
    void loadItemsLoadsAllRegisteredItems() {
        TestStorableConfigurationItem<String> firstItem = createStringItem("first-key", "initial-first");
        TestStorableConfigurationItem<Integer> secondItem = createIntegerItem("second-key", 123);

        storageProvider.storedValues.put("first-key", "loaded-first");
        storageProvider.storedValues.put("second-key", 456);

        registry.register(firstItem);
        registry.register(secondItem);

        registry.getAccessor().loadItems();

        assertEquals("loaded-first", firstItem.getCurrentValue());
        assertEquals(456, secondItem.getCurrentValue());
        assertEquals(2, storageProvider.loadCount);
    }

    @Test
    void loadItemsWithSpecificIdsLoadsOnlySelectedItems() {
        TestStorableConfigurationItem<String> firstItem = createStringItem("first-key", "initial-first");
        TestStorableConfigurationItem<Integer> secondItem = createIntegerItem("second-key", 123);
        TestStorableConfigurationItem<String> thirdItem = createStringItem("third-key", "initial-third");

        storageProvider.storedValues.put("first-key", "loaded-first");
        storageProvider.storedValues.put("second-key", 456);
        storageProvider.storedValues.put("third-key", "loaded-third");

        registry.register(firstItem);
        registry.register(secondItem);
        registry.register(thirdItem);

        //noinspection RedundantArrayCreation
        registry.getAccessor().loadItems(new Object[]{"first-key", "second-key"});

        assertEquals("loaded-first", firstItem.getCurrentValue());
        assertEquals(456, secondItem.getCurrentValue());
        assertEquals("initial-third", thirdItem.getCurrentValue());
        assertEquals(2, storageProvider.loadCount);
    }

    @Test
    void loadItemsWithSpecificIdsThrowsWhenOneIdIsMissing() {
        registry.register(createStringItem("first-key", "value"));

        ConfigurationItemsRegistryAccessor accessor = registry.getAccessor();
        //noinspection RedundantArrayCreation
        assertThrows(NoSuchElementException.class, () -> accessor.loadItems(new Object[]{"first-key", "missing-key"}));
    }

    @Test
    void loadItemsWithSpecificIdsThrowsWhenIdsArrayIsNull() {
        ConfigurationItemsRegistryAccessor accessor = registry.getAccessor();
        assertThrows(NullPointerException.class, () -> accessor.loadItems((Object[]) null));
    }

    @Test
    void loadItemsWithSpecificIdsThrowsWhenOneIdIsNull() {
        registry.register(createStringItem("first-key", "value"));

        ConfigurationItemsRegistryAccessor accessor = registry.getAccessor();
        //noinspection RedundantArrayCreation
        assertThrows(NullPointerException.class, () -> accessor.loadItems(new Object[]{"first-key", null}));
    }

    @Test
    void saveItemsSavesAllRegisteredItems() {
        TestStorableConfigurationItem<String> firstItem = createStringItem("first-key", "first");
        TestStorableConfigurationItem<Integer> secondItem = createIntegerItem("second-key", 123);

        registry.register(firstItem);
        registry.register(secondItem);

        registry.getAccessor().saveItems();

        assertEquals("first", storageProvider.storedValues.get("first-key"));
        assertEquals(123, storageProvider.storedValues.get("second-key"));
        assertEquals(2, storageProvider.storeCount);
    }

    @Test
    void saveItemsWithSpecificIdsSavesOnlySelectedItems() {
        TestStorableConfigurationItem<String> firstItem = createStringItem("first-key", "first");
        TestStorableConfigurationItem<Integer> secondItem = createIntegerItem("second-key", 123);
        TestStorableConfigurationItem<String> thirdItem = createStringItem("third-key", "third");

        registry.register(firstItem);
        registry.register(secondItem);
        registry.register(thirdItem);

        //noinspection RedundantArrayCreation
        registry.getAccessor().saveItems(new Object[]{"first-key", "third-key"});

        assertEquals("first", storageProvider.storedValues.get("first-key"));
        assertNull(storageProvider.storedValues.get("second-key"));
        assertEquals("third", storageProvider.storedValues.get("third-key"));
        assertEquals(2, storageProvider.storeCount);
    }

    @Test
    void saveItemsWithSpecificIdsThrowsWhenOneIdIsMissing() {
        registry.register(createStringItem("first-key", "value"));

        ConfigurationItemsRegistryAccessor accessor = registry.getAccessor();
        //noinspection RedundantArrayCreation
        assertThrows(NoSuchElementException.class, () -> accessor.saveItems(new Object[]{"first-key", "missing-key"}));
    }

    @Test
    void saveItemsWithSpecificIdsThrowsWhenIdsArrayIsNull() {
        ConfigurationItemsRegistryAccessor accessor = registry.getAccessor();
        assertThrows(NullPointerException.class, () -> accessor.saveItems((Object[]) null));
    }

    @Test
    void saveItemsWithSpecificIdsThrowsWhenOneIdIsNull() {
        registry.register(createStringItem("first-key", "value"));

        ConfigurationItemsRegistryAccessor accessor = registry.getAccessor();
        //noinspection RedundantArrayCreation
        assertThrows(NullPointerException.class, () -> accessor.saveItems(new Object[]{"first-key", null}));
    }

    @Test
    void loadItemsByKeyLoadsSelectedItems() {
        ConfigurationItemKey<String> firstKey = ConfigurationItemKey.of("first-key", String.class);
        ConfigurationItemKey<Integer> secondKey = ConfigurationItemKey.of("second-key", Integer.class);

        TestStorableConfigurationItem<String> firstItem = createStringItem("first-key", "initial-first");
        TestStorableConfigurationItem<Integer> secondItem = createIntegerItem("second-key", 123);
        TestStorableConfigurationItem<String> thirdItem = createStringItem("third-key", "initial-third");

        storageProvider.storedValues.put("first-key", "loaded-first");
        storageProvider.storedValues.put("second-key", 456);
        storageProvider.storedValues.put("third-key", "loaded-third");

        registry.register(firstItem);
        registry.register(secondItem);
        registry.register(thirdItem);

        registry.getAccessor().loadItemsByKey(firstKey, secondKey);

        assertEquals("loaded-first", firstItem.getCurrentValue());
        assertEquals(456, secondItem.getCurrentValue());
        assertEquals("initial-third", thirdItem.getCurrentValue());
        assertEquals(2, storageProvider.loadCount);
    }

    @Test
    void loadItemsByKeyThrowsWhenOneKeyIsMissing() {
        ConfigurationItemKey<String> firstKey = ConfigurationItemKey.of("first-key", String.class);
        ConfigurationItemKey<String> missingKey = ConfigurationItemKey.of("missing-key", String.class);
        registry.register(createStringItem("first-key", "value"));

        ConfigurationItemsRegistryAccessor accessor = registry.getAccessor();
        assertThrows(NoSuchElementException.class, () -> accessor.loadItemsByKey(firstKey, missingKey));
    }

    @Test
    void loadItemsByKeyThrowsWhenKeysArrayIsNull() {
        ConfigurationItemsRegistryAccessor accessor = registry.getAccessor();
        assertThrows(NullPointerException.class, () -> accessor.loadItemsByKey((ConfigurationItemKey<?>[]) null));
    }

    @Test
    void loadItemsByKeyThrowsWhenOneKeyIsNull() {
        ConfigurationItemKey<String> firstKey = ConfigurationItemKey.of("first-key", String.class);
        registry.register(createStringItem("first-key", "value"));

        ConfigurationItemsRegistryAccessor accessor = registry.getAccessor();
        assertThrows(NullPointerException.class, () -> accessor.loadItemsByKey(firstKey, null));
    }

    @Test
    void saveItemsByKeySavesSelectedItems() {
        ConfigurationItemKey<String> firstKey = ConfigurationItemKey.of("first-key", String.class);
        ConfigurationItemKey<String> thirdKey = ConfigurationItemKey.of("third-key", String.class);

        TestStorableConfigurationItem<String> firstItem = createStringItem("first-key", "first");
        TestStorableConfigurationItem<Integer> secondItem = createIntegerItem("second-key", 123);
        TestStorableConfigurationItem<String> thirdItem = createStringItem("third-key", "third");

        registry.register(firstItem);
        registry.register(secondItem);
        registry.register(thirdItem);

        registry.getAccessor().saveItemsByKey(firstKey, thirdKey);

        assertEquals("first", storageProvider.storedValues.get("first-key"));
        assertNull(storageProvider.storedValues.get("second-key"));
        assertEquals("third", storageProvider.storedValues.get("third-key"));
        assertEquals(2, storageProvider.storeCount);
    }

    @Test
    void saveItemsByKeyThrowsWhenOneKeyIsMissing() {
        ConfigurationItemKey<String> firstKey = ConfigurationItemKey.of("first-key", String.class);
        ConfigurationItemKey<String> missingKey = ConfigurationItemKey.of("missing-key", String.class);
        registry.register(createStringItem("first-key", "value"));

        ConfigurationItemsRegistryAccessor accessor = registry.getAccessor();
        assertThrows(NoSuchElementException.class, () -> accessor.saveItemsByKey(firstKey, missingKey));
    }

    @Test
    void saveItemsByKeyThrowsWhenKeysArrayIsNull() {
        ConfigurationItemsRegistryAccessor accessor = registry.getAccessor();
        assertThrows(NullPointerException.class, () -> accessor.saveItemsByKey((ConfigurationItemKey<?>[]) null));
    }

    @Test
    void saveItemsByKeyThrowsWhenOneKeyIsNull() {
        ConfigurationItemKey<String> firstKey = ConfigurationItemKey.of("first-key", String.class);
        registry.register(createStringItem("first-key", "value"));

        ConfigurationItemsRegistryAccessor accessor = registry.getAccessor();
        assertThrows(NullPointerException.class, () -> accessor.saveItemsByKey(firstKey, null));
    }

    @Test
    void registryIsThreadSafe() throws InterruptedException {
        int threadCount = 10;
        int itemsPerThread = 100;
        Thread[] threads = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            final int threadIndex = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < itemsPerThread; j++) {
                    String key = "key-" + threadIndex + "-" + j;
                    registry.register(createStringItem(key, "value"));
                    registry.getItem(key);
                    registry.unregisterById(key);
                }
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        assertEquals(0, registry.size());
    }

    private TestStorableConfigurationItem<String> createStringItem(String key, String currentValue) {
        return new TestStorableConfigurationItem<>(key, String.class, currentValue, storageProvider);
    }

    @SuppressWarnings("SameParameterValue")
    private TestStorableConfigurationItem<Integer> createIntegerItem(String key, Integer currentValue) {
        return new TestStorableConfigurationItem<>(key, Integer.class, currentValue, storageProvider);
    }

    private static final class TestStorageProvider implements StorageProvider {

        private final Map<Object, Object> storedValues = new HashMap<>();

        private int storeCount;
        private int loadCount;

        @Override
        public <I, K, A, P> void storeConfigurationItem(StorableConfigurationItem<I, K, A, P> item) {
            storeCount++;
            storedValues.put(item.getKey(), item.getCurrentValue());
        }

        @SuppressWarnings("unchecked")
        @Override
        public <I, K, A, P> void loadConfigurationItem(StorableConfigurationItem<I, K, A, P> item) {
            loadCount++;

            if (storedValues.containsKey(item.getKey())) {
                item.setCurrentValue((A) storedValues.get(item.getKey()));
            }
        }

        @Override
        public boolean isKeyClassTypeAllowed(Class<?> keyClass) {
            return true;
        }

        @Override
        public boolean isStorageClassTypeAllowed(Class<?> valueClass) {
            return true;
        }
    }

    private static final class TestStorableConfigurationItem<A> implements StorableConfigurationItem<String, String, A, A> {

        private final String key;
        private final Class<A> valueType;
        private final StorageProvider storageProvider;

        private A currentValue;
        private boolean changed;

        private TestStorableConfigurationItem(String key, Class<A> valueType, A currentValue, StorageProvider storageProvider) {
            this.key = key;
            this.valueType = valueType;
            this.currentValue = currentValue;
            this.storageProvider = storageProvider;
        }

        @Override
        public Class<String> intKeyType() {
            return String.class;
        }

        @Override
        public Class<String> extKeyType() {
            return String.class;
        }

        @Override
        public Class<A> applicationDataType() {
            return valueType;
        }

        @Override
        public Class<A> storageType() {
            return valueType;
        }

        @Override
        public Function<String, String> toExtKey() {
            return Function.identity();
        }

        @Override
        public Function<A, A> toDataType() {
            return Function.identity();
        }

        @Override
        public Function<A, A> toStorageType() {
            return Function.identity();
        }

        @Override
        public @NonNull StorageProvider storageProvider() {
            return storageProvider;
        }

        @Override
        public @NotNull String getKey() {
            return key;
        }

        @Override
        public void setCurrentValue(A value) {
            if (!Objects.equals(currentValue, value)) {
                currentValue = value;
                changed = true;
            }
        }

        @Override
        public @Nullable A getCurrentValue() {
            return currentValue;
        }

        @Override
        public boolean hasUnsavedChanges() {
            return changed;
        }

        @Override
        public void clearUnsavedChanges() {
            changed = false;
        }

        @Override
        public @Nullable A getDefaultValue() {
            return null;
        }
    }

    @Test
    void hasUnsavedChangesReturnsFalseForNewItem() {
        ConfigurationItemKey<String> key = ConfigurationItemKey.of("test-key", String.class);
        TestStorableConfigurationItem<String> item = createStringItem("test-key", "value");

        registry.register(item);

        assertFalse(registry.getAccessor().hasUnsavedChanges(key));
    }

    @Test
    void hasUnsavedChangesReturnsTrueAfterValueChange() {
        ConfigurationItemKey<String> key = ConfigurationItemKey.of("test-key", String.class);
        TestStorableConfigurationItem<String> item = createStringItem("test-key", "before");

        registry.register(item);

        registry.getAccessor().setCurrentValue(key, "after");

        assertTrue(registry.getAccessor().hasUnsavedChanges(key));
    }

    @Test
    void hasUnsavedChangesReturnsFalseWhenSettingSameValue() {
        ConfigurationItemKey<String> key = ConfigurationItemKey.of("test-key", String.class);
        TestStorableConfigurationItem<String> item = createStringItem("test-key", "value");

        registry.register(item);

        registry.getAccessor().setCurrentValue(key, "value");

        assertFalse(registry.getAccessor().hasUnsavedChanges(key));
    }

    @Test
    void hasUnsavedChangesReturnsFalseWhenItemIsMissing() {
        ConfigurationItemKey<String> key = ConfigurationItemKey.of("missing-key", String.class);

        assertFalse(registry.getAccessor().hasUnsavedChanges(key));
    }

    @Test
    void hasRequiredUnsavedChangesThrowsWhenItemIsMissing() {
        ConfigurationItemKey<String> key = ConfigurationItemKey.of("missing-key", String.class);

        ConfigurationItemsRegistryAccessor accessor = registry.getAccessor();
        assertThrows(NoSuchElementException.class, () -> accessor.hasRequiredUnsavedChanges(key));
    }

    @Test
    void loadClearsUnsavedChanges() {
        ConfigurationItemKey<String> key = ConfigurationItemKey.of("test-key", String.class);
        TestStorableConfigurationItem<String> item = createStringItem("test-key", "before");

        registry.register(item);
        registry.getAccessor().setCurrentValue(key, "changed");

        assertTrue(registry.getAccessor().hasUnsavedChanges(key));

        storageProvider.storedValues.put("test-key", "loaded");
        registry.getAccessor().loadItemsByKey(key);

        assertEquals("loaded", item.getCurrentValue());
        assertFalse(registry.getAccessor().hasUnsavedChanges(key));
    }

    @Test
    void saveClearsUnsavedChanges() {
        ConfigurationItemKey<String> key = ConfigurationItemKey.of("test-key", String.class);
        TestStorableConfigurationItem<String> item = createStringItem("test-key", "before");

        registry.register(item);
        registry.getAccessor().setCurrentValue(key, "changed");

        assertTrue(registry.getAccessor().hasUnsavedChanges(key));

        registry.getAccessor().saveItemsByKey(key);

        assertEquals("changed", storageProvider.storedValues.get("test-key"));
        assertFalse(registry.getAccessor().hasUnsavedChanges(key));
    }

    @Test
    void addListenerAddsListenerToRegistry() {
        List<ConfigurationItemRegistryEvent> events = new ArrayList<>();
        ConfigurationItemRegistryListener listener = events::add;

        registry.addListener(listener);

        TestStorableConfigurationItem<String> item = createStringItem("test-key", "value");
        registry.register(item);

        assertEquals(1, events.size());
        assertEquals(ConfigurationItemRegistryEventType.REGISTERED, events.getFirst().type());
        assertEquals("test-key", events.getFirst().key());
        assertSame(item, events.getFirst().item());
    }

    @Test
    void removeListenerRemovesListenerFromRegistry() {
        List<ConfigurationItemRegistryEvent> events = new ArrayList<>();
        ConfigurationItemRegistryListener listener = events::add;

        registry.addListener(listener);
        registry.removeListener(listener);

        TestStorableConfigurationItem<String> item = createStringItem("test-key", "value");
        registry.register(item);

        assertTrue(events.isEmpty());
    }

    @Test
    void registerNotifiesRegisteredEvent() {
        List<ConfigurationItemRegistryEvent> events = new ArrayList<>();
        registry.addListener(events::add);

        TestStorableConfigurationItem<String> item = createStringItem("test-key", "value");
        registry.register(item);

        assertEquals(1, events.size());
        ConfigurationItemRegistryEvent event = events.getFirst();
        assertEquals(ConfigurationItemRegistryEventType.REGISTERED, event.type());
        assertEquals("test-key", event.key());
        assertSame(item, event.item());
        assertNull(event.previousItem());
    }

    @Test
    void replaceNotifiesReplacedEvent() {
        TestStorableConfigurationItem<String> firstItem = createStringItem("test-key", "first");
        registry.register(firstItem);

        List<ConfigurationItemRegistryEvent> events = new ArrayList<>();
        registry.addListener(events::add);

        TestStorableConfigurationItem<String> secondItem = createStringItem("test-key", "second");
        registry.replace(secondItem);

        assertEquals(1, events.size());
        ConfigurationItemRegistryEvent event = events.getFirst();
        assertEquals(ConfigurationItemRegistryEventType.REPLACED, event.type());
        assertEquals("test-key", event.key());
        assertSame(secondItem, event.item());
        assertSame(firstItem, event.previousItem());
    }

    @Test
    void unregisterNotifiesUnregisteredEvent() {
        TestStorableConfigurationItem<String> item = createStringItem("test-key", "value");
        registry.register(item);

        List<ConfigurationItemRegistryEvent> events = new ArrayList<>();
        registry.addListener(events::add);

        registry.unregister(ConfigurationItemKey.of("test-key", String.class));

        assertEquals(1, events.size());
        ConfigurationItemRegistryEvent event = events.getFirst();
        assertEquals(ConfigurationItemRegistryEventType.UNREGISTERED, event.type());
        assertEquals("test-key", event.key());
        assertSame(item, event.item());
    }

    @Test
    void unregisterByIdNotifiesUnregisteredEvent() {
        TestStorableConfigurationItem<String> item = createStringItem("test-key", "value");
        registry.register(item);

        List<ConfigurationItemRegistryEvent> events = new ArrayList<>();
        registry.addListener(events::add);

        registry.unregisterById("test-key");

        assertEquals(1, events.size());
        ConfigurationItemRegistryEvent event = events.getFirst();
        assertEquals(ConfigurationItemRegistryEventType.UNREGISTERED, event.type());
        assertEquals("test-key", event.key());
        assertSame(item, event.item());
    }

    @Test
    void clearNotifiesClearedEvent() {
        registry.register(createStringItem("test-key", "value"));

        List<ConfigurationItemRegistryEvent> events = new ArrayList<>();
        registry.addListener(events::add);

        registry.clear();

        assertEquals(1, events.size());
        assertEquals(ConfigurationItemRegistryEventType.CLEARED, events.getFirst().type());
    }

    @Test
    void clearDoesNotNotifyIfEmpty() {
        List<ConfigurationItemRegistryEvent> events = new ArrayList<>();
        registry.addListener(events::add);

        registry.clear();

        assertTrue(events.isEmpty());
    }

    @Test
    void setCurrentValueNotifiesValueChangedEvent() {
        ConfigurationItemKey<String> key = ConfigurationItemKey.of("test-key", String.class);
        TestStorableConfigurationItem<String> item = createStringItem("test-key", "before");
        registry.register(item);

        List<ConfigurationItemRegistryEvent> events = new ArrayList<>();
        registry.addListener(events::add);

        registry.getAccessor().setCurrentValue(key, "after");

        assertEquals(1, events.size());
        ConfigurationItemRegistryEvent event = events.getFirst();
        assertEquals(ConfigurationItemRegistryEventType.VALUE_CHANGED, event.type());
        assertEquals("test-key", event.key());
        assertSame(item, event.item());
    }

    @Test
    void setCurrentValueDoesNotNotifyIfValueIsSame() {
        ConfigurationItemKey<String> key = ConfigurationItemKey.of("test-key", String.class);
        TestStorableConfigurationItem<String> item = createStringItem("test-key", "same");
        registry.register(item);

        List<ConfigurationItemRegistryEvent> events = new ArrayList<>();
        registry.addListener(events::add);

        registry.getAccessor().setCurrentValue(key, "same");

        assertTrue(events.isEmpty());
    }

    @Test
    void loadItemsNotifiesLoadedEvent() {
        TestStorableConfigurationItem<String> item = createStringItem("test-key", "before");
        registry.register(item);

        List<ConfigurationItemRegistryEvent> events = new ArrayList<>();
        registry.addListener(events::add);

        registry.getAccessor().loadItems();

        assertEquals(1, events.size());
        ConfigurationItemRegistryEvent event = events.getFirst();
        assertEquals(ConfigurationItemRegistryEventType.LOADED, event.type());
        assertEquals("test-key", event.key());
        assertSame(item, event.item());
    }

    @Test
    void saveItemsNotifiesSavedEvent() {
        TestStorableConfigurationItem<String> item = createStringItem("test-key", "before");
        registry.register(item);

        List<ConfigurationItemRegistryEvent> events = new ArrayList<>();
        registry.addListener(events::add);

        registry.getAccessor().saveItems();

        assertEquals(1, events.size());
        ConfigurationItemRegistryEvent event = events.getFirst();
        assertEquals(ConfigurationItemRegistryEventType.SAVED, event.type());
        assertEquals("test-key", event.key());
        assertSame(item, event.item());
    }

    @Test
    void notifyListenersHandlesExceptions() {
        List<ConfigurationItemRegistryEvent> receivedEvents = new ArrayList<>();
        ConfigurationItemRegistryListener failingListener = _ -> {
            throw new RuntimeException("Test exception");
        };
        ConfigurationItemRegistryListener workingListener = receivedEvents::add;

        registry.addListener(failingListener);
        registry.addListener(workingListener);

        registry.register(createStringItem("test-key", "value"));

        assertEquals(1, receivedEvents.size());
        assertEquals(ConfigurationItemRegistryEventType.REGISTERED, receivedEvents.getFirst().type());
    }
}
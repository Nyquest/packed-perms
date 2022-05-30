package kz.kesh.packed_perms;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PackedPermsTest {

    @Test
    void pack1() {
        Set<Integer> set = new HashSet<>();
        set.add(17);
        set.add(4);
        set.add(85);
        assertEquals(PackedPerms.pack(set), "CABAAAAAAAAAAAQ");
    }

    @Test
    void pack2() {
        List<Integer> list = new ArrayList<>();
        list.add(17);
        list.add(4);
        list.add(85);
        assertEquals(PackedPerms.pack(list), "CABAAAAAAAAAAAQ");
    }

    @Test
    void pack3() {
        List<Integer> list = new ArrayList<>();
        int size = 10_008;
        for (int i = 0; i < size; i++) {
            list.add(i);
        }
        char[] arr = new char[size / 6];
        Arrays.fill(arr, '_');
        assertEquals(PackedPerms.pack(list), new String(arr));
    }

    @Test
    void toBinaryString1() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                PackedPerms.toBinaryString("CABAAAAAAAAAAAQ@"));
    }

    @Test
    void toBinaryString2() {
        assertEquals(PackedPerms.toBinaryString("CABAAAAAAAAAAAQ"), "000010000000000001000000000000000000000000000000000000000000000000000000000000000000010000");
    }

    @Test
    void toBinaryString3() {
        List<Integer> list = new ArrayList<>();
        list.add(17);
        list.add(4);
        list.add(85);
        String bin = PackedPerms.toBinaryString(PackedPerms.pack(list));
        List<Integer> resultList = new ArrayList<>();
        for (int i = 0; i < bin.length(); i++) {
            if(bin.charAt(i) == '1') {
                resultList.add(i);
            }
        }
        Collections.sort(list);
        assertEquals(list, resultList);
    }

    @Test
    void hasPermission1() {
        String pack = PackedPerms.pack(Collections.singletonList(17));
        assertTrue(PackedPerms.hasPermission(pack, 17));
    }

    @Test
    void hasPermission2() {
        List<Integer> permissionIds = Arrays.asList(17, 4, 85);
        String pack = PackedPerms.pack(permissionIds);
        for (Integer permissionId : permissionIds) {
            assertTrue(PackedPerms.hasPermission(pack, permissionId));
        }
    }

    @Test
    void hasPermission3() {
        Set<Integer> set = new HashSet<>();
        for (int i = 0; i < 10_000; i++) {
            set.add(i);
        }
        String pack = PackedPerms.pack(set);
        for (Integer permissionId : set) {
            assertTrue(PackedPerms.hasPermission(pack, permissionId));
        }
    }

    @Test
    void hasPermission4() {
        Random random = new Random();
        for (int t = 0; t < 1000; t++) {
            Set<Integer> set = new HashSet<>();
            int size = 1000 + random.nextInt(9_000);
            for (int i = 0; i < size; i++) {
                set.add(random.nextInt(10_000));
            }
            String pack = PackedPerms.pack(set);
            for (Integer permissionId : set) {
                assertTrue(PackedPerms.hasPermission(pack, permissionId));
            }
        }
    }

    @Test
    void hasPermission5() {
        Random random = new Random();
        Set<Integer> set = new HashSet<>();
        for (int i = 0; i < 5_000; i++) {
            set.add(random.nextInt(10_000));
        }
        String pack = PackedPerms.pack(set);
        for (int t = 0; t < 10_000_000; t++) {
            int permissionId = random.nextInt(11_000);
            assertEquals(set.contains(permissionId), PackedPerms.hasPermission(pack, permissionId));
        }
    }

    @Test
    void hasPermission6() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> PackedPerms.hasPermission("A", -1));
    }

    @Test
    void hasPermission7() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> PackedPerms.hasPermission("A", -10));
    }

    @Test
    void hasAnyPermission2() {
        List<Integer> permissionIds = Arrays.asList(17, 4, 85);
        String pack = PackedPerms.pack(permissionIds);
        assertFalse(PackedPerms.hasAnyPermission(pack, 0, 1, 2));
    }


}
package kz.kesh.packed_perms;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;

/**
 * Data structure for packing permission identifiers in the base 64 for transmission inside the JWT.
 * Each permission number is represented by one bit.
 */
public class PackedPerms {

    private static final char[] TO_BASE_64_URL = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '_'
    };

    private static final int[] FROM_BASE_64_URL = new int[256];

    static {
        Arrays.fill(FROM_BASE_64_URL, -1);
        for (int i = 0; i < TO_BASE_64_URL.length; ++i) {
            FROM_BASE_64_URL[TO_BASE_64_URL[i]] = i;
        }
    }

    /**
     * Packing permission ids in base64 format
     * @param permissionIds permission identifiers (for example: 0, 1, 2, ...)
     * @return permissions packed in Base64
     */
    public static String pack(Collection<Integer> permissionIds) {
        Integer max = null;
        for (Integer permissionId : permissionIds) {
            if(permissionId == null) {
                throw new NullPointerException("permissionId is null");
            }
            if(max == null || max < permissionId) {
                max = permissionId;
            }
        }
        if(max == null) {
            return "";
        }
        ++max;
        int remainder = max % 6;
        if(remainder != 0) {
            max += 6 - remainder;
        }
        BitSet bitSet = new BitSet(max);
        for (Integer permissionId : permissionIds) {
            bitSet.set(permissionId);
        }
        StringBuilder sb = new StringBuilder(max / 6);
        for (int i = 0; i < max; i += 6) {
            int num = 0;
            for (int j = 0; j < 6; ++j) {
                if(bitSet.get(i + j)) {
                    num |= 1 << (6 - j - 1);
                }
            }
            sb.append(TO_BASE_64_URL[num]);
        }
        return sb.toString();
    }

    /**
     * Determines if the packed permissions has a particular permission identifier
     * @param packedPerms packed permissions
     * @param permissionId permission identifier
     * @return true if the permission is found, else false
     */
    public static boolean hasPermission(String packedPerms, int permissionId) {
        if(permissionId < 0) {
            throw new IllegalArgumentException("PermissionId must be non-negative");
        }
        int index = permissionId / 6;
        if(index < packedPerms.length()) {
            char ch = packedPerms.charAt(index);
            if(FROM_BASE_64_URL[ch] == -1) {
                throw new IllegalArgumentException("Incorrect symbol: " + ch + " (" + (int) ch + ")");
            }
            int num = FROM_BASE_64_URL[ch];
            int remainder = permissionId % 6;
            int mask = 1 << (6 - remainder - 1);
            return (num & mask) == mask;
        }
        return false;
    }

    /**
     * Determines if the packed permissions has any of the specified permission identifiers
     * @param packedPerms packed permissions
     * @param permissionIds permission identifiers
     * @return true if any of the permissions is found, else false
     */
    public static boolean hasAnyPermission(String packedPerms, int... permissionIds) {
        for (int permissionId : permissionIds) {
            if(hasPermission(packedPerms, permissionId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Convert Base64 to binary string
     * @param value packed permissions in Base64 format
     * @return binary string
     */
    public static String toBinaryString(String value) {
        StringBuilder sb = new StringBuilder();
        int mask;
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            if(FROM_BASE_64_URL[ch] == -1) {
                throw new IllegalArgumentException("Incorrect symbol: " + ch + " (" + (int) ch + ")");
            }
            int num = FROM_BASE_64_URL[ch];
            for (int j = 0; j < 6; j++) {
                mask = 1 << (6 - j - 1);
                sb.append((num & mask) == mask ? 1 : 0);
            }
        }
        return sb.toString();
    }

}

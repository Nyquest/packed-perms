### Motivation
When creating information systems with a microservice architecture, it is desirable that user authentication and authorization be sessionless (stateless).

Using JWT tokens is not a bad solution. The token may contain a username, various information about him, including his permissions.

But there is one unpleasant moment. If our system has a lot of permissions, for example, several hundred, then passing the permission codes in the token will increase its size. By itself, this is not critical, but the token is passed in the request header, which is limited from 4K to 8K by default in various web servers and web proxies. You can, of course, increase the size of the header, but all the same, reasonable restrictions may not be enough, especially since each such request will be of a significant size. Further, the option of transferring a larger number of permissions in the token will be proposed.

### Implementation
To begin with, you need to assign unique sequential numbering to all permissions - 0, 1, 2, etc. (it is not necessary to start the numbering from 0, gaps in the numbering are allowed).

The idea is that each permission number will be assigned a bit in the bitmap.

If there is a permission, then the bit at the index with the permission number will be set.

The bits will be converted to a Base64 string. Further, this string will be added to the token as a value for field "permissions" (for example).

One character in Base64 encodes 6 bits.

Thus, 10 thousand permissions will borrow approximately 1667 bytes.

10,000 bits / 6 = 1667 bytes.
But this is not the final size. Due to the fact that the structural parts of the token are encoded in Base64, the size will increase by 4/3. The total size of 10 thousand permissions transferred as part of the token will be 2.17 KB.

1667 * 4 / 3 = 2223 bytes (2.17 KB).

![](https://kesh.kz/blog/wp-content/uploads/2022/05/packed-perms.drawio.png)

### Permissions packing
The pack static method packs the user's permission identifiers into a Base64 string.
```
List<Integer> list = Arrays.asList(2, 4, 7, 8, 9, 10, 12, 14, 15, 
        1000, 2005, 10002, 10007);
String pack = PackedPerms.pack(list);
System.out.println(pack);
```
The result will be a string with a length of 1668 characters:
```
KesAAAAAAA...AAAAAh
```

### Permission check
The check is carried out by the static methods hasPermission and hasAnyPermission.

hasPermission - checks for a single permission

hasAnyPermission - checks for at least one permission from the list.
```
List<Integer> permissionIds = Arrays.asList(16, 4, 101);
String pack = PackedPerms.pack(permissionIds);
System.out.println(PackedPerms.hasPermission(pack, 16)); // true
System.out.println(PackedPerms.hasAnyPermission(pack, 100, 101, 102)); // true
```

The hasPermission method runs in constant O(1) time without allocating additional memory.

The hasAnyPermission method runs in O(k) linear time without allocating additional memory.

Where k is the number of checked permission and usually it is not large.

### Example of use
In the test class JwtTest.java, you can see an example of using the PackedPerms along with the Jwt token.
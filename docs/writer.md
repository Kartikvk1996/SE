## Writer 

`[writer.cpp]`

This module is a bit complex that's why explained here. The writer module basically performs a single operation

* Indexing words.

The indexing is done in following way.

1. Word given is hashed.
    ```c
    h = whash(word);    /* /include/proto/phashes/phash.cpp */
    ```
2. Err... wait there is no perfect hash function isn't it? That's why we use indirection. You can tune the levels of indirection from the constants defined in the `writer.cpp`. But what kind of indirection we are going to use? In each level we point to the next file where we are going to find the key for next level. In the final level we are going to do a linear search for the word. [Are you thinking of the binary search there? Well it performs the same as linear search. But wait! we can also choose the search function].

3. Hey you are going to open the files multiple times right? Can we eliminate this file opening and closing time. Yeah! __caching..__ But what kind of buffers you are going to use for caching. Three different or a single one? Suppose we use a single buffer we may get multiple hits may end up with the lots of replacements as multiple clashes are expected due to the last level and it's very complex to maintain fileids.

4. Ok Ok enough! tell me about the fileids. These are constructed by concatinating key at every level. __And so there is a limit on indirection `4` as we are limiting the table size to 2<sup>16</sup> and there are 4 pairs of bytes in 64 bytes__.

5. What about hash functions we will use different hash functions for different levels. This ensures we will not end up with the same offsets if we use the tables of same size at each level.

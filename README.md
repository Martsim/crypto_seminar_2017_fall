# Code for report on Asymmetric Numeral Systems

Do note, that the Python implementation could contain some misses and has been tested to work in rather small code block lengths.

The Java implementation uses a double check. After encoding, it checks that decoding would give the original text. Otherwise would throw a RuntimeException. 

Huffman would fail when the dictionary contains only one element. Therefore, the encoding function, which creates a dictionary based on the input would fail in that case.

See [Java use guide](https://github.com/Martsim/crypto_seminar_2017_fall/wiki/Java-use-guide) for information on how to run the Java code.

See [Python use guide](https://github.com/Martsim/crypto_seminar_2017_fall/wiki/Python-use-guide) for information on how to run the Python code.

Huffman code implementation is based on [huffman.py by M. Reid](https://gist.github.com/mreid/fdf6353ec39d050e972b).

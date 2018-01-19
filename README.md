# Code for report on Asymmetric Numeral Systems

Do note, that the Python implementation could contain some misses and has been tested to work in rather small code block lengths.

The java implementation uses a double check. After encoding, it checks that decoding would give the original text. Otherwise would throw a RuntimeException. 

Huffman would fail when the dictionary contains only one element. Therefore, the encoding function, which creates a dictionary based on the input would fail in that case.

# rANS is the rangedANS example code
# Huffman is the Huffman coding example which was based on
# https://gist.github.com/mreid/fdf6353ec39d050e972b
# Also given in the report
from rANS import encode_text_with_dict 
from huffman import hc_encode

rANS_bigger = 0
HC_bigger = 0
equal = 0
nr_tests = 0
sum_diff = 0
avg_diff = 0.0
sum_HC = 0.0
sum_rANS = 0.0
avg_HC = 0.0
avg_rANS = 0.0

# Runs compression tests on input text with given block size
# and calculates the average compression ratios for both algorithms.
def run_tests(block_size=100, text_file_name='dickens'):
    global avg_diff, avg_rANS, avg_HC, sum_HC, sum_rANS
    txt = ""
    with open(text_file_name) as f: #Filename for input text 
        txt = f.read(block_size)
        while txt.__len__() > block_size - 2:
            compare_two(txt)
            txt = f.read(block_size)
    avg_diff = float(sum_diff)/nr_tests
    avg_rANS = round(sum_rANS/nr_tests , 5) * 100 
    avg_HC = round(sum_HC/nr_tests , 5) * 100 

# Compares Huffman coding with ranged ANS.
# Prints results that show the compressed text size for both algorithms and their difference.
def compare_two(text):
    global HC_bigger, rANS_bigger, equal, nr_tests, sum_diff, sum_HC, sum_rANS
    nr_tests = nr_tests + 1
    print "Original text length: " + str(text.__len__())
    orig = float(text.__len__())
    xx1 = encode_text_with_dict(text)
    binar = str(bin(int(xx1)))[2:]
    rans_baits = binar.__len__() / 8
    print "rANS coded in bytes: " + str(rans_baits)
    
    xx = hc_encode(text)
    
    #evaluate
    HC_baits = xx.__len__() / 8
    print "HC coded in bytes: " + str(HC_baits)
    diff = HC_baits - rans_baits
    print "Difference between HC and rans results is: " + str(diff) + " bytes"
    print ""
     
    if HC_baits > rans_baits:
        HC_bigger = HC_bigger + 1
    elif HC_baits < rans_baits:
        rANS_bigger = rANS_bigger + 1
    else:
        equal = equal + 1
    sum_diff = sum_diff + diff
    sum_HC = sum_HC + round(float(HC_baits) / orig, 5)
    sum_rANS = sum_rANS + round(float(rans_baits) / orig, 5)
    
run_tests()
print
print "Statistics"
print "rANS encoded text was_bigger than Huffman encoded text " + str(rANS_bigger) + " number of times"
print "Huffman Coding was bigger than rANS encoded text " + str(HC_bigger) + " number of times"
print "Encoded texts were equal " + str(equal) + " number of times"
print "The number of tests run: " + str(nr_tests)

# Huffman encoded text size in bytes minus rANS encoded text size in bytes
print "The average_difference in bytes between Huffman encoded"
print " and rANS encoded text: " + str(avg_diff)

print "The average compression ratio for rANS " + str(avg_rANS)
print "The average compression ratio for HC " + str(avg_HC)

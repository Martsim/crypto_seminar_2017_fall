
from math import floor

letter_to_num = {'a':0, 'e':1, 'i':2, 'o':3, 'u':4, '!':5}
num_to_letter = {0:'a', 1:'e', 2:'i', 3:'o', 4:'u', 5:'!'}

NROFELEMENTS = 6
M = 10 #the base
ls={0:2, 1:3, 2:1, 3:2, 4:1, 5:1} # Length of symbols range
bs={0:0, 1:2, 2:5, 3:6, 4:8, 5:9} # Beginning of symbols range

# Returns symbol from x
def s(x):
    return help_for_s(x % M)

# Helper function for cleaner code. Used by s(x)
def help_for_s(x):
    sofarmin = -1
    that_s = -1
    for s in range(0, NROFELEMENTS):
        thissum = 0
        for i in range(0, s + 1):
            thissum = thissum + ls[i]  
        
        if thissum > x:
            if thissum < sofarmin or sofarmin == -1:
                sofarmin = thissum
                that_s = s
                
    return that_s

# Takes a symbol s and encodes it to the state x.
def encode(s, x):
    return M * floor( x / ls[s] ) + bs[s] + (x % ls[s])

#decodes symbol s and state x_s from given state x.
def decode(x):
    current_s = s(x)
    return (current_s, ls[current_s] * floor( x / M ) + (x % M)  - bs[current_s])

#Text and the initial state of the encoder
def encode_text(text, x=0):
    for symbol in text:
        x = encode(letter_to_num[symbol], x)
    x = encode(letter_to_num['!'], x)
    return x

# Prepares the ranges based on the training text.
def build_dict(text):
    global letter_to_num, num_to_letter, NROFELEMENTS, ls, bs, M 
    letter_to_num = {}
    num_to_letter = {}
    NROFELEMENTS = 0    
    ls={}
    bs = {}
    
    for symbol in text:
        if not symbol in letter_to_num.keys():
            nr = NROFELEMENTS
            NROFELEMENTS = NROFELEMENTS + 1
            letter_to_num[symbol] = nr
            num_to_letter[nr] = symbol
            ls[nr] = 1
        else:
            nr = letter_to_num[symbol]
            ls[nr] = ls[nr] + 1
    bs[0] = 0
    M = 0
    for i in range(1, NROFELEMENTS):
        bs[i] = bs[i-1] + ls[i-1]
        M = M + ls[i-1] 
    M = M + ls[NROFELEMENTS - 1]
    #print "The system is of base " + str(M)

# Initializes the range for this specific text and then encodes the text.
def encode_text_with_dict(text, x=0):
    build_dict(text)
    for symbol in text:
        x = encode(letter_to_num[symbol], x)
    return x


def encodemessage_demo():
    x2 = encode(1, 1)
    print "encoded e to 1 ", x2
    x2 = encode(0, x2)
    print "encoded a to prev ", x2
    x2 = encode(2, x2)
    print "encoded i to prev ", x2
    x2 = encode(2, x2)
    print "encoded i to prev ", x2
    x2 = encode(5, x2)
    print "encoded ! to prev ", x2
    return x2

def encodemessage2_demo():
    x2 = encode(1, 1)
    print "encoded e to 1 ", x2
    x2 = encode(1, x2)
    print "encoded e to prev ", x2
    x2 = encode(1, x2)
    print "encoded e to prev ", x2
    
def decode_message_demo(x):
    (s,x) = decode(x)
    print "decoded ", num_to_letter[s] , " new x ", x
    (s,x) = decode(x)
    print "decoded ", num_to_letter[s] , " new x ", x
    (s,x) = decode(x)
    print "decoded ", num_to_letter[s] , " new x ", x
    (s,x) = decode(x)
    print "decoded ", num_to_letter[s] , " new x ", x
    (s,x) = decode(x)
    print "decoded ", num_to_letter[s] , " new x ", x

# Encodes the input text
def demo_encode_text():
    text = "aeaeouuiiiuiui"
    xx = encode_text(text)
    print xx

# Encodes the input text with range specific to this text.
def demo_encode_text_with_dict():
    text = "aeaeouuiiiuiui"
    xx = encode_text_with_dict(text)
    print xx

# Demo based on Dickens. Reads 200 characters, initializes range for this specific text and encodes it. 
def demo_dickens():
    txt = ""
    with open('dickens') as f:
        txt = f.read(200)
    print "Text contains" + str(len(txt)) + " characters"
    xx = encode_text_with_dict(txt)
    print "Resulting code: " + str(xx)
    binar = str(bin(int(xx)))[2:]
    print "In bytes: " + str(binar.__len__() / 8)
    
# Comment in any of the following lines to test the encoding/decoding process
#decode_message_demo(encodemessage_demo())
#encodemessage2_demo()

#demo_encode_text()
#demo_encode_text_with_dict()
#demo_dickens()

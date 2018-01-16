# Example Huffman coding implementation
# Distributions are represented as dictionaries of { 'symbol': probability }
# Codes are dictionaries too: { 'symbol': 'codeword' }

def huffman(p):
    '''Return a Huffman code for an ensemble with distribution p.'''
    #assert(sum(p.values()) == 1.0) # Ensure probabilities sum to 1

    # Base case of only two symbols, assign 0 or 1 arbitrarily
    if(len(p) == 2):
        return dict(zip(p.keys(), ['0', '1']))

    # Create a new distribution by merging lowest prob. pair
    p_prime = p.copy()
    a1, a2 = lowest_prob_pair(p)
    p1, p2 = p_prime.pop(a1), p_prime.pop(a2)
    p_prime[a1 + a2] = p1 + p2

    # Recurse and construct code on new distribution
    c = huffman(p_prime)
    ca1a2 = c.pop(a1 + a2)
    c[a1], c[a2] = ca1a2 + '0', ca1a2 + '1'

    return c

def lowest_prob_pair(p):
    '''Return pair of symbols from distribution p with lowest probabilities.'''
    assert(len(p) >= 2) # Ensure there are at least 2 symbols in the dist.

    sorted_p = sorted(p.items(), key=lambda (i,pi): pi)
    return sorted_p[0][0], sorted_p[1][0]

def create_prob_dist(text):
    prob_dist = {}
    counts = {}
    total = 0
    for s in text + "!":
        if s in counts.keys():
            counts[s] = counts[s] + 1
            total = total + 1
        else:
            counts[s] = 1
            total = total + 1
    for k in counts.keys():
        prob_dist[k] = round(float(counts[k]) / float(total), 5)
    
    summer = 0.0
    summer = sum(prob_dist.values())
    if summer != 1.0:
        diff = 1.0 - summer
        #print "Difference " + str(diff)
        use_key = prob_dist.keys()[0]
        #print "Key " + use_key
        prob_dist[use_key] = round(prob_dist[use_key] + diff, 5)
    return prob_dist

def hc_encode(text):
    prob_dist = create_prob_dist(text)
    #print prob_dist
    huffman_tree = huffman(prob_dist)
    #print huffman_tree
    code_to = ""
    for s in text:
        code_to = code_to + huffman_tree[s]
    code_to = code_to# + huffman_tree["!"]
    return code_to 
        

def demo_dickens():
    txt = ""
    with open('dickens') as f:
        txt = f.read(200)
    print "Text contains" + str(len(txt)) + " characters"
    xx = hc_encode(txt)
    print "Resulting code: " + str(xx)
    #evaluate
    baits = xx.__len__() / 8
    print baits
    

# Example execution
#ex1 = { 'a': 0.5, 'b': 0.25, 'c': 0.25 }
#a = huffman(ex1) # => {'a': '0', 'c': '10', 'b': '11'}
#print a
#ex2 = { 'a': 0.25, 'b': 0.25, 'c': 0.2, 'd': 0.15, 'e': 0.15 }
#a = huffman(ex2)  # => {'a': '01', 'c': '00', 'b': '10', 'e': '110', 'd': '111'}
#print a

#print hc_encode("abaaaabacbbb")

#print hc_encode("aeaeouuiiiuiui")
#demo_dickens()
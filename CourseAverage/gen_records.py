import random
import sys

if __name__=='__main__':

    avg = {}
    courses = ['COL703', 'COL733', 'COL768', 'COL783', 'COL100']
    for c in courses:
        avg[c] = 0
    
    
    f = open(sys.argv[2],'w')
    
    n = int(sys.argv[1])
    
    for i in range(1,n+1):
      for course in courses:
        score = random.randint(0,100)
        f.write("{} {} {}\n".format(i,course,score))
        
        avg[course] += score//n
    
    f.close()
    print(avg)
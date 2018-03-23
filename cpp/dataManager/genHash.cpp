
#include<iostream>
using namespace std;
long genHash(const char *string){

    long  sum=0;
	int i=0;
    while(string[i]){sum+=string[i]*i;i++;

    }
sum-=string[i/2];
return sum;
}


int main(){
cout<< genHash("Hello World")<<"\n";
}



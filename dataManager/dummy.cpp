 
#include<iostream>
#include<string>

using namespace std;

int main(){cout<<up("hi there how are you")<<"\n";}
void up(string str){
                vector<string> strings;
		vector<string> :: iterator it;
                string temp="";
                int i=0;
                for(;str[i]!=0;i++){
                        if(str[i]==' ')strings.push_front(temp);
                        temp+=str[i]);}

		for(it=string.begin();it!=string.end();it++){cout<<*it;cout<<"\n";}


	}

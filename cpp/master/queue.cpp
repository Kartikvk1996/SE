#include<iostream>
#include<cstdio>
#include<cstdlib>
#include<iostream>
#include<fstream>
#include "../include/debug.h"

#define MAXSIZE 1000
/*
 * Node Declaration
 */
using namespace std;
struct node
{
    unsigned long int id;
    string URL;
    unsigned short int depth;
    struct node *next;
    struct node *prev;
}*head;


class myQueue
{
    public:
        bool create_list(unsigned long int id,string URL,unsigned short int depth);

        int count();
        void display_queue();
        struct node* getLinks(int reqSize);
        int storeToFile();
        void readFromFile(int size);

        myQueue()
        {
            head = NULL;
        }
};


//
//bool myQueue::create_list(a,b,c){
//    s=new node(a,b,c)
//}



bool myQueue::create_list(unsigned long int id,string URL,unsigned short int depth)
{
    /*
        json jobj = json::parse(str);
        string url = jobj["url"].get<string>();
        uint id = jobj["id"].get<int>();
        ushort depth = jobj["depth"].get<ushort>();
    */

    struct node *s, *temp;
    int currsize = this->myQueue::count();
    if(currsize<=MAXSIZE)
    {

       temp = new(struct node);
       temp->id=id;
       temp->URL=URL;
       temp->depth=depth;
       temp->next = NULL;
       if (head == NULL)
       {
        temp->prev = NULL;
        head = temp;
        return 1;
       }
       else
       {
        s = head;
        while (s->next != NULL && s->id!=temp->id)
        {

                  s = s->next;

        }
        if(s->id == temp->id)
            return 0;

        else{
            s->next = temp;
            temp->prev = s;
            temp->next=NULL;

            return 1;

             }
     }
    }
    else
    {
      cout<<"Queue Overflow"<<endl;
      return 0;
    }

}




struct node * myQueue :: getLinks(int reqSizee)
{
    int currsize = this->myQueue::count();
    struct node * arrayOfLinks= (struct node *)malloc(reqSizee * sizeof(node));
    int reqSize=reqSizee;
    if(reqSize<=currsize)
   {
    struct node * current;
    int itr=0;
    current=head;

    while (itr<reqSize)
    {
    arrayOfLinks[itr].id = current->id;

    arrayOfLinks[itr].URL = current->URL;
    arrayOfLinks[itr].depth = current->depth;


    current = current->next;
    itr++;
    }

     cout<<arrayOfLinks[2].id<<" <-> ";
    cout<<"links returnned"<<endl;
     }
    else{
        cout<<"no links returnned"<<endl;

    }

     /*  cout<<arrayOfLinks[0].id<<" <-> ";
                    cout<<arrayOfLinks[0].URL<<" <-> ";
                    cout<<arrayOfLinks[0].depth<<" <-> "<<endl;
*/

        return arrayOfLinks;

}


void myQueue::storeToFile()
{
    struct node * start = head;
    FILE *fp = fopen("url.data", "w");
    while(start) {
        fprintf(fp, "%d %d %s\n", start->id, data->depth, data->URL.c_str());
        start=start->next;
    }
    fclose(fp);
}



void  myQueue::readFromFile(int size)
{
    FILE *fp = fopen("url.data", "r");

    char buffer[1024];
    unsigned long int id;
    unsigned short int depth;
    while((fscanf(fp, "%d%d%s", &id, &depth, buffer)))
        create_list(id, string(buffer), depth);
}


int myQueue::count()
{
    return cnt;
}

void myQueue::display_queue()
{
    struct node *q;
    if (head == NULL)
    {
        cout<<"List empty,nothing to display"<<endl;
        return;
    }
    q = head;
    cout<<"QUEUE:"<<endl;
    while (q != NULL)
    {
        cout<<q->id<<" <-> ";
        cout<<q->URL<<" <-> ";
        cout<<q->depth<<" <-> "<<endl;

        q = q->next;
    }
    cout<<"NULL"<<endl;
}


int main()
{
    int choice,size;

    unsigned long int id;
    string URL;
    unsigned short int depth;

    myQueue q;
    while (1)
    {
        cout<<endl<<"----------------------------"<<endl;
        cout<<endl<<"Operations on Doubly linked list"<<endl;
        cout<<endl<<"----------------------------"<<endl;
        cout<<"1.Create Node"<<endl;
        cout<<"2.display list"<<endl;
        cout<<"3.get Links"<<endl;
        cout<<"4.store to file"<<endl;
        cout<<"5.read file"<<endl;

        cout<<"6.count"<<endl;


        cin>>choice;
        switch ( choice )
        {
        case 1:
            cout<<"Enter the id: ";
            cin>>id;
             cout<<"Enter the URL: ";
            cin>>URL;
             cout<<"Enter the depth: ";
            cin>>depth;
            q.create_list(id,URL,depth);
            cout<<endl;
            break;
        case 2:
            q.display_queue();
            cout<<endl;
            break;
        case 3:{

                  int nolinks,i;
                  cout<<"enter no. of links"<<endl;
                  cin>>nolinks;

                  struct node * arrayOfLinks= (struct node *)malloc(sizeof(node));



                  arrayOfLinks=q.getLinks(nolinks);
                   cout<<"main"<<endl;


                /* for(i=1;i<2;i++)
                 {
                    cout<<arrayOfLinks[i].id<<" <-> ";
                    cout<<arrayOfLinks[i].URL<<" <-> ";
                    cout<<arrayOfLinks[i].depth<<" <-> "<<endl;
                 }*/
                 break;
                }

        case 4:size=q.storeToFile();
               break;
        case 5:q.readFromFile(size);
               break;
        case 6:{int c=q.count();
                 cout<<c<<endl;
                 break; }
        default:
              cout<<"Wrong choice"<<endl;
        }
    }
    return 0;
}

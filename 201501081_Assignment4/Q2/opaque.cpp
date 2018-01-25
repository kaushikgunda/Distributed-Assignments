#include<iostream>
using namespace std;
int main()
{
int i;
for(i=0;i<10;i++)
if(i%2==0)
cout << "\u25A0" <<" ";
else
cout << "\u25A1" << " ";
}

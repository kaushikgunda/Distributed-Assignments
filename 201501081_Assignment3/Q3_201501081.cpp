#include<mpi.h>
#include<iostream>
#include <fstream>
#include<vector>
#include <string>
#include <queue> 
using namespace std;
struct edge
{
	int a;
	int b;
	int weight;
};
bool operator<(const edge& a, const edge& b) {
	return a.weight > b.weight;
}
int Find(int parent[], int i)
{
	if (parent[i] == -1)
		return i;
	return Find(parent, parent[i]);
}
void Union(int parent[], int x, int y)
{
	int xset = Find(parent, x);
	int yset = Find(parent, y);
	parent[xset] = yset;
} 
int main(int argc, char** argv)
{
	MPI_Init(&argc, &argv);
	int world_size;
	MPI_Comm_size(MPI_COMM_WORLD, &world_size);
	int rank;
	MPI_Comm_rank(MPI_COMM_WORLD, &rank);
	if(rank==0)
	{
		int v,e;
		cin >> v >> e;
		int i;
		int parent[v+1];
		for(i=1;i<=v;i++)
			parent[i]=-1;
		edge edgex[e];
		for(i=0;i<e;i++)
		{
			int a,b,w;
			cin >> a >> b >> w;
			edgex[i].a=a;
			edgex[i].b=b;
			edgex[i].weight=w;
		}
		//Now send the edges to the respective processors
		int total_process=world_size-1;
		int temp=0;
		for(i=1;i<total_process;i++)
		{
			int to_sen=e/total_process;
			int j;
			MPI_Send(&to_sen,1,MPI_INT,i,0,MPI_COMM_WORLD);
			for(j=0;j<to_sen;j++)
			{
				int arr[3];
				arr[0]=edgex[temp].a;
				arr[1]=edgex[temp].b;
				arr[2]=edgex[temp].weight;
				temp++;
				MPI_Send(&arr,3,MPI_INT,i,0,MPI_COMM_WORLD);
			}

		}
		int to_sen=e-temp;
		MPI_Send(&to_sen,1,MPI_INT,i,0,MPI_COMM_WORLD);
		for(int j=temp;j<e;j++)
		{
			int arr[3];
			arr[0]=edgex[temp].a;
			arr[1]=edgex[temp].b;
			arr[2]=edgex[temp].weight;
			temp++;
			MPI_Send(&arr,3,MPI_INT,total_process,0,MPI_COMM_WORLD);
		}
		int recv=0;
		int tot_weight=0;
		while(recv<(v-1))
		{
			int least[3];
			int proc_at=-1;
			 least[2]=100000000;
			int i;
			//Receiving the least edges from each processors
			for(i=1;i<=total_process;i++)
			{
				int arr[3];
				
				MPI_Recv(&arr,3,MPI_INT,i,0,MPI_COMM_WORLD,MPI_STATUS_IGNORE);	
				if(arr[2]<least[2])
				{
					least[0]=arr[0];
					least[1]=arr[1];
					least[2]=arr[2];
					proc_at=i;
				}
			}
			
			//Now check if this forms a cycle or not
			if(Find(parent,least[0])!=Find(parent,least[1]))
			{
				Union(parent,least[0],least[1]);
					recv+=1;
				tot_weight+=least[2];
			}
			if(recv<v-1)
			{
				//Send only to the process proc_at to change it and rest all to retain the edge
				int j;
				for(j=1;j<=total_process;j++)
				{
					if(j!=proc_at)
					{
						int flag=2;
						//Send all of them to continue flag =2 
						MPI_Send(&flag,1,MPI_INT,j,0,MPI_COMM_WORLD);
					}
					else
					{
						int flag=3;
						MPI_Send(&flag,1,MPI_INT,j,0,MPI_COMM_WORLD);		
					}
					//Give do not change signal flag=3
				}

			}
			else
			{
				for(int j=1;j<=total_process;j++)
				{
					int flag=4;
					MPI_Send(&flag,1,MPI_INT,j,0,MPI_COMM_WORLD);
				}
				//Send message to all processes to stop
			}

		}
	cout << tot_weight << endl;
	}
	else 
	{
		int e;
		//Number of edges
		MPI_Recv(&e,1,MPI_INT,0,0,MPI_COMM_WORLD,MPI_STATUS_IGNORE);
		int i;
		edge edgex[e];
		priority_queue<edge> heap;

		for(i=0;i<e;i++)
		{
			int arr[3];
			MPI_Recv(&arr,3,MPI_INT,0,0,MPI_COMM_WORLD,MPI_STATUS_IGNORE);
			edgex[i].a=arr[0];
			edgex[i].b=arr[1];
			edgex[i].weight=arr[2];
			heap.push(edgex[i]);	
		}

		int flag=0;
		while(flag==0)
		{
			//Get the edge with least weight
			if(heap.empty())
			{
 int arr[3];
                        arr[0]=1;
                        arr[1]=1;
                        arr[2]=1000000000;

MPI_Send(&arr,3,MPI_INT,0,0,MPI_COMM_WORLD);
			}	
			else
			{
			edge least=heap.top();
			int arr[3];
			arr[0]=least.a;
			arr[1]=least.b;
			arr[2]=least.weight;
			//Send the array
			MPI_Send(&arr,3,MPI_INT,0,0,MPI_COMM_WORLD);
			}
			//Now wait for the flag
			int x;
			MPI_Recv(&x,1,MPI_INT,0,0,MPI_COMM_WORLD,MPI_STATUS_IGNORE);	
			if(x==4)
			break;
			if(x==3)
			{
				heap.pop();
			}	

			//Receive the flag
			//if stop flag get out of the while loop
		}
	}
	MPI_Finalize();
}

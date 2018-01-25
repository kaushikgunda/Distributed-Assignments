#include<mpi.h>
#include<iostream>
#include<vector>
#include <cstdlib>
#include <bits/stdc++.h>
using namespace std;
int comp(const void*a,const void*b)
{
	return *(int*)a-*(int*)b;
}
struct node
{
	int val;
	int arrno;
	int pos;
};
struct compare{
	bool operator()(const node& a, const node& b){
		return a.val > b.val;
	}
};
int main(int argc, char** argv)
{
	MPI_Init(&argc, &argv);
	int world_size;
	MPI_Comm_size(MPI_COMM_WORLD, &world_size);
	int rank;
	MPI_Comm_rank(MPI_COMM_WORLD, &rank);
	int n;
	vector<int> arr; 
	if (rank == 0) // Master
	{
		cin >> n;
		int i;
		for(i=0;i<n;i++)
		{
			int x;
			cin >> x;
			arr.push_back(x);
		}
		int temp=0;
		int total_process=world_size-1;
		int limit[total_process+1];
		for(i=1;i<total_process;i++)
		{

			MPI_Send(&arr[temp],n/total_process,MPI_INT,i,0,MPI_COMM_WORLD);
			temp+=n/total_process;
			limit[i]=temp-1;		
		}
		int start=temp;
		int end=n;
		MPI_Send(&arr[temp],end-start,MPI_INT,total_process,0,MPI_COMM_WORLD);
		limit[i]=end-1;
		int max_len=max(end-start,n/total_process);
		int Recieved[total_process+1][max_len];
		for(i=1;i<=total_process;i++)
		{int j;
			for(j=0;j<max_len;j++)
			{
				Recieved[i][j]=100000000;

			}

		}
		priority_queue <node, vector<node>, compare > min_heap;
		//Now receive from all of them into a single array and merge it using min heap
		for(i=1;i<=total_process;i++)
		{
			MPI_Status status;
			int number_amount;
			MPI_Probe(i, 0, MPI_COMM_WORLD, &status);
			MPI_Get_count(&status, MPI_INT, &number_amount);
			int temp[number_amount];
			MPI_Recv(&temp,number_amount,MPI_INT,i,0,MPI_COMM_WORLD,MPI_STATUS_IGNORE);
			int j;
			for(j=0;j<number_amount;j++)
			{

				Recieved[i][j]=temp[j];
			}
			
			limit[i]=number_amount-1;
		}
		limit[0]=0;
		//Now you have 'total_process' sorted arrays
		//Now you have merge them and print the sorted array
		for(i=1;i<=total_process;i++)
		{
			node temp;
			temp.val=Recieved[i][0];
			temp.arrno=i;
			temp.pos=0;
			min_heap.push(temp);

		}


		for(i=1;i<=n;i++)
		{
			node temp=min_heap.top();
			cout << temp.val << " " ;
			int pos=temp.pos+1;
			min_heap.pop();
			if(pos<=limit[temp.arrno])
			{
				node ne;
				ne.val=Recieved[temp.arrno][pos];
				ne.arrno=temp.arrno;
				ne.pos=pos;
				min_heap.push(ne);
			}


		}
	cout << endl;


	}
	else
	{
		MPI_Status status;
		int number_amount;
		MPI_Probe(0, 0, MPI_COMM_WORLD, &status);
		MPI_Get_count(&status, MPI_INT, &number_amount);
		int temp[number_amount];
		MPI_Recv(&temp,number_amount,MPI_INT,0,0,MPI_COMM_WORLD,MPI_STATUS_IGNORE);
		int i;
	qsort(temp,number_amount,sizeof(temp[0]),comp);

/*		if(rank==world_size-1)
		{
		for(i=0;i<number_amount;i++)
		cout << temp[i] << endl;
		}
*/
		/*	cout << "Rank of the process is " << rank << endl;
			for(i=0;i<number_amount;i++)
			cout << temp[i] << " " ;
			cout << endl;
		 */

		MPI_Send(&temp,number_amount,MPI_INT,0,0,MPI_COMM_WORLD);
	}


	MPI_Finalize();
}



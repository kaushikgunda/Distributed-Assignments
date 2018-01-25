#include<mpi.h>
#include<fstream>
#include<iostream>
using namespace std;
int check(int i,int n)
{
	if(i>=0 && i <= (n-1))
		return 1;
	return 0;
}

int main(int argc, char** argv)
{

	MPI_Init(&argc, &argv);
	int world_size;
	MPI_Comm_size(MPI_COMM_WORLD, &world_size);
	int rank;int n=5;
	MPI_Comm_rank(MPI_COMM_WORLD, &rank);
	if(rank==0)
	{
		//Take input from a file
		string seed=string("seed.txt");
		ifstream fil;
		fil.open(seed.c_str());
		int i;
		int it;
		cin >> it;
		int arr[n+2][n];
		for(i=0;i<n;i++)
		arr[0][i]=0;
		for(i=1;i<=n;i++)
		{
			int j;
			string x;
			fil >> x;
			for(j=0;j<x.length();j++)
				arr[i][j]=x[j]-'0';

		}
		for(i=0;i<n;i++)
		arr[n+1][i]=0;
		fil.close();
		/*	for(i=0;i<n;i++)
			{
			int j;
			for(j=0;j<n;j++)
			cout << arr[i][j];
			cout << endl;
			}
		 */
		int total_process=world_size-1;
		//The total number of rows have to be divided into these many 
		int rows=n/total_process;
		int rowsL=rows+n%total_process;
		int temp=0;
		int start[n];
		for(i=1;i<=total_process;i++)
		{
			MPI_Send(&it,1,MPI_INT,i,0,MPI_COMM_WORLD);

		}
		int itcount=0;
		while(it--)
		{
			itcount++;
			int tempx=1;
			for(i=1;i<=total_process;i++)
			{
				start[i]=tempx;
				tempx+=rows;
			}
			for(i=1;i<total_process;i++)	
			{
				//Send "rows" number of rows to i'th process
				//Send two rows extra one from top and one from bottom
				int rowsx=rows+2;
				MPI_Send(&rowsx,1,MPI_INT,i,0,MPI_COMM_WORLD);
				MPI_Send(&n,1,MPI_INT,i,0,MPI_COMM_WORLD);
				int j;
			//	MPI_Send(&arr[start[i]-1],n,MPI_INT,i,0,MPI_COMM_WORLD);
				for(j=start[i]-1;j<start[i]+rows+1;j++)
				{
					MPI_Send(&arr[j],n,MPI_INT,i,0,MPI_COMM_WORLD);

				}
			//	MPI_Send(&arr[start[i+1]],n,MPI_INT,i,0,MPI_COMM_WORLD);	
			}
			//For the last process;
			int rowsLx=rowsL+2;
			MPI_Send(&rowsLx,1,MPI_INT,i,0,MPI_COMM_WORLD);
			MPI_Send(&n,1,MPI_INT,i,0,MPI_COMM_WORLD);
			//cout << start[i]-1 << start[i]+rows+1 << endl;	
			for(int j=start[i]-1;j<start[i]+rowsL+1;j++)
				MPI_Send(&arr[j],n,MPI_INT,total_process,0,MPI_COMM_WORLD);
			//Receiving portion
			int temp=1;
			for(i=1;i<=total_process;i++)
			{
				int rec;
				MPI_Recv(&rec,1,MPI_INT,i,0,MPI_COMM_WORLD,MPI_STATUS_IGNORE);
				int j;
				for(j=0;j<rec;j++)
				{
					int ar[n];
					MPI_Recv(&ar,n,MPI_INT,i,0,MPI_COMM_WORLD,MPI_STATUS_IGNORE);
					int l;
					for(l=0;l<n;l++)
						arr[temp][l]=ar[l];
					temp++;	
				}
			}
			cout << "For iteration: " << itcount << endl; 
			for(i=1;i<=n;i++)
			{
				int j;
				for(j=0;j<n;j++)
				{
					//		cout << arr[i][j];
					if(arr[i][j]%2==1)
						cout << "\u25A0" << " ";
					else
						cout << "\u25A1" << " " ;
				}
				cout << endl;
			}

		}
	}
	else
	{
		int it;
		MPI_Recv(&it,1,MPI_INT,0,0,MPI_COMM_WORLD,MPI_STATUS_IGNORE);
		while(it--)
		{
			int rows;
			int n;
			MPI_Recv(&rows,1,MPI_INT,0,0,MPI_COMM_WORLD,MPI_STATUS_IGNORE);
			MPI_Recv(&n,1,MPI_INT,0,0,MPI_COMM_WORLD,MPI_STATUS_IGNORE);
			int arr[rows][n];int i;
			//finalx is the final array that should be sent
			int finalx[rows][n];
			int activex[rows][n];
			for(i=0;i<rows;i++)
			{
				MPI_Recv(&arr[i],n,MPI_INT,0,0,MPI_COMM_WORLD,MPI_STATUS_IGNORE);
				int j;
				for(j=0;j<n;j++)
				{
					finalx[i][j]=0;
					activex[i][j]=0;
				}

			}
			//Calculate it through some variatons and send it back  
			//The final matrix is calculated for rows 1 to rows-2 only and only that part is sent back
			//Now follow all the 4 rules and decide the matrix around 
			//first calculate just the active number of cells around each cell
			for(i=1;i<rows-1;i++)
			{
				for(int j=0;j<n;j++)
				{
					if(check(i,rows) && check(j+1,n)) activex[i][j]+=arr[i][j+1];
					if(check(i,rows) && check(j-1,n)) activex[i][j]+=arr[i][j-1];
					if(check(i+1,rows) && check(j,n)) activex[i][j]+=arr[i+1][j]; 		
					if(check(i-1,rows) && check(j,n)) activex[i][j]+=arr[i-1][j];
					if(check(i+1,rows) && check(j+1,n)) activex[i][j]+=arr[i+1][j+1];
					if(check(i+1,rows) && check(j-1,n)) activex[i][j]+=arr[i+1][j-1];
					if(check(i-1,rows) && check(j+1,n)) activex[i][j]+=arr[i-1][j+1];
					if(check(i-1,rows) && check(j-1,n)) activex[i][j]+=arr[i-1][j-1];

				}


			}
			for(i=1;i<rows-1;i++)
			{
				for(int j=0;j<n;j++)
				{
					if(arr[i][j]==1 )
					{
						if(activex[i][j]>=2 && activex[i][j]<=3)
							finalx[i][j]=1;
						else
							finalx[i][j]=0;
					}
					else
						if(arr[i][j]==0)
						{
							if(activex[i][j] ==3)
								finalx[i][j]=1;
							else
								finalx[i][j]=0;
						}

				}
			}		

			//The final matrix is transformed and finally sent back
			int sendx=rows-2;
			MPI_Send(&sendx,1,MPI_INT,0,0,MPI_COMM_WORLD);
			/*	cout << "For process" << rank << "iteration" << it << endl;
				int j;
				for(i=1;i<rows-1;i++)
				{
				for(j=0;j<n;j++)
				cout << arr[i][j]  << finalx[i][j] << " " ;
				cout << endl;
				}
			 */	
			for(i=1;i<rows-1;i++)
				MPI_Send(&finalx[i],n,MPI_INT,0,0,MPI_COMM_WORLD);

		}
	}
	MPI_Finalize();
}


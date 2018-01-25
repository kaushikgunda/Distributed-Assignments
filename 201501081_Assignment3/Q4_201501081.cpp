#include<mpi.h>
#include<iostream>
#include <fstream>
#include<vector>
#include <string>
using namespace std;
int main(int argc, char** argv)
{
	MPI_Init(&argc, &argv);
	int world_size;
	MPI_Comm_size(MPI_COMM_WORLD, &world_size);
	int rank;
	MPI_Comm_rank(MPI_COMM_WORLD, &rank);
	if(rank==0)
	{

		char* filename=argv[1];
		string pattern=argv[2];	
		string line;
		ifstream myfile (filename);
		vector<string> text;
		if (myfile.is_open())
		{
			while ( getline (myfile,line) )
			{
				text.push_back(line);
			}
			myfile.close();
		}

		int total_lines=text.size();

		int total_process=world_size-1;

		int i;
		int tot=0;
		int temp=0;
		int to_sen=0;

		for(int i=1;i<=total_process-1;i++)
		{
			//Send the pattern first
			int pattern_len=pattern.size();
			MPI_Send(&pattern_len,1,MPI_INT,i,0,MPI_COMM_WORLD);
			MPI_Send(pattern.c_str(),pattern_len,MPI_BYTE,i,0,MPI_COMM_WORLD);	
			to_sen=total_lines/total_process;
			int j;
			MPI_Send(&to_sen,1,MPI_INT,i,0,MPI_COMM_WORLD);
			tot+=to_sen;

			for(j=1;j<=to_sen;j++)
			{
				int line_size=text[temp].size();
				//int line_size=1;
				MPI_Send(&line_size,1,MPI_INT,i,0,MPI_COMM_WORLD);
				MPI_Send(text[temp].c_str(),line_size,MPI_BYTE,i,0,MPI_COMM_WORLD);
				temp+=1;
			}



		}
		to_sen=total_lines-temp;
		int j;
		int pattern_len=pattern.size();
		MPI_Send(&pattern_len,1,MPI_INT,total_process,0,MPI_COMM_WORLD);
		MPI_Send(pattern.c_str(),pattern_len,MPI_BYTE,total_process,0,MPI_COMM_WORLD);

		MPI_Send(&to_sen,1,MPI_INT,total_process,0,MPI_COMM_WORLD);
		for(j=to_sen;j<=total_lines;j++)
		{
			int line_size=text[temp].size();
			MPI_Send(&line_size,1,MPI_INT,total_process,0,MPI_COMM_WORLD);
			MPI_Send(text[temp].c_str(),line_size,MPI_BYTE,total_process,0,MPI_COMM_WORLD);

			temp+=1;
		}

		int count=0;
		for(i=1;i<=total_process;i++)
		{
			int match;
			//Number of lines matched
			MPI_Recv(&match,1,MPI_INT,i,0,MPI_COMM_WORLD,MPI_STATUS_IGNORE);
			int j;
			count+=match;
			for(j=1;j<=match;j++)
			{
				//Number of characters in the string to be sent;
				int line_size;
				MPI_Recv(&line_size,1,MPI_INT,i,0,MPI_COMM_WORLD,MPI_STATUS_IGNORE);
				char *buf = new char[line_size];
				MPI_Recv(buf,line_size,MPI_CHAR,i,0,MPI_COMM_WORLD,MPI_STATUS_IGNORE);
				string temp(buf,line_size);
				cout << temp << endl;
			}


		}

		cout << "total=" << count << endl;

	}
	else
	{
		//Lenth of pattern
		int pattern_len;
		vector<string> match;
		MPI_Recv(&pattern_len,1,MPI_INT,0,0,MPI_COMM_WORLD,MPI_STATUS_IGNORE);
		char *pat=new char[pattern_len];

		MPI_Recv(pat,pattern_len,MPI_CHAR,0,0,MPI_COMM_WORLD,MPI_STATUS_IGNORE);

		string str2(pat,pattern_len);


		//Total lines to be received;
		int total_lines=0;

		MPI_Recv(&total_lines,1,MPI_INT,0,0,MPI_COMM_WORLD,MPI_STATUS_IGNORE);
		int i;
		for(i=1;i<=total_lines;i++)
		{
			int line_size;
			MPI_Recv(&line_size,1,MPI_INT,0,0,MPI_COMM_WORLD,MPI_STATUS_IGNORE);
			char *buf = new char[line_size];
			MPI_Recv(buf,line_size,MPI_CHAR,0,0,MPI_COMM_WORLD,MPI_STATUS_IGNORE);
			string str(buf,line_size);
			size_t found = str.find(str2);
			if (found!=std::string::npos)
				match.push_back(str);
		}
		int match_len=match.size();

		MPI_Send(&match_len,1,MPI_INT,0,0,MPI_COMM_WORLD);
		for(i=1;i<=match_len;i++)
		{
			int line_size=match[i-1].size();
			MPI_Send(&line_size,1,MPI_INT,0,0,MPI_COMM_WORLD);
			MPI_Send(match[i-1].c_str(),line_size,MPI_BYTE,0,0,MPI_COMM_WORLD);


		}

	}
	MPI_Finalize();
}

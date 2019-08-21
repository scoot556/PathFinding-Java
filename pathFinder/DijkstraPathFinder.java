package pathFinder;

import java.util.*;

import map.Coordinate;
import map.PathMap;

public class DijkstraPathFinder implements PathFinder
{
	private PathMap dispMap;
	private int coordsExplored=0;
	private int[][] totalWeight = null;
	private Coordinate[][] prevCoord = null;
	private List<Coordinate> backPath = new ArrayList<Coordinate>();
	private List<Coordinate> visitCoord = new ArrayList<Coordinate>();
	private List<Coordinate> toVisit=new ArrayList<Coordinate>();
	private List<Coordinate> shortPath = new ArrayList<Coordinate>();
	
    public DijkstraPathFinder(PathMap map) 
    {
    	dispMap = map;
    	getDetails(dispMap);
    	setCost(true);
    }
    
    
    public List<Coordinate> findPath() 
    {
    	List<Coordinate> path=new ArrayList<Coordinate>();

    	if(dispMap.waypointCells.size() != 0)
    	{
    		for(Coordinate coord : dispMap.waypointCells)
    		{
    			runPath(coord);
    			if(path.size() > 0) {
    				path.remove(path.size()-1);
    			}
    			path.addAll(getShortPath(coord));
    			setCost(false);
    			totalWeight[coord.getRow()][coord.getColumn()]=0;
    		}
    		if(path.size()>0) {
    			path.remove(path.size()-1);
    		path.addAll(getShortPath(dispMap.waypointCells.get(dispMap.waypointCells.size()-1)));
    		}
    		
    		
    		setCost(false);
    		Coordinate coord=dispMap.waypointCells.get(dispMap.waypointCells.size()-1);
			totalWeight[coord.getRow()][coord.getColumn()]=0;
			runPath(null);
			
			List<Coordinate> destPath=null;
			int destCost = 1000;
			for(int i = 0 ; i < dispMap.destCells.size(); ++i) {
				List<Coordinate> totalPath = getShortPath(dispMap.destCells.get(i));
				if(destPath == null || getCost(totalPath)<destCost) {
					destPath = totalPath;
					destCost = getCost(totalPath);
				}
			}
			if(path.size() > 0) {
    			path.remove(path.size()-1);
    		path.addAll(destPath);
			}
    	}
    	else
    	{
    		if(dispMap.destCells.size() != 0) {
    			runPath(dispMap.destCells.get(0));
    			}
    		int minDistance = 1000;
    		for(Coordinate coord : dispMap.destCells){
    			List<Coordinate> temp = getShortPath(coord);
    			if(getCost(temp) < minDistance){
    				minDistance = getCost(temp);
    				path = temp;
    			}
    		}
    	}
        return path;
    }

    private void runPath(Coordinate dest)
    {
    	List<Coordinate> toVisit = notVisit();
    	
        int count = 0;
    	
    	while(count != toVisit.size())
    	{	
    		Coordinate curr = getShortDist(toVisit);
    		toVisit.remove(curr);
    		List<Coordinate> neigh = getToVisit(toVisit,getNeigh(curr));
    		//coordsExplored++;
    		
    		if(dest != null && curr.equals(dest))
    		{
    			break;
    		}
    		for(Coordinate nb : neigh)
    		{
    			int weight = totalWeight[curr.getRow()][curr.getColumn()] + dispMap.cells[nb.getRow()][nb.getColumn()].getTerrainCost();
    			if(weight < totalWeight[nb.getRow()][nb.getColumn()])
    			{
    				totalWeight[nb.getRow()][nb.getColumn()] = weight;
    				prevCoord[nb.getRow()][nb.getColumn()] = curr;
    			}
    		}
    	}
    }

    private List<Coordinate> getShortPath(Coordinate dest)
    {
    	Coordinate curr = dest;
    	while(curr!=null)
    	{
    		backPath.add(curr);
    		curr=prevCoord[curr.getRow()][curr.getColumn()];
    	}
    	for(int i = backPath.size()-1; i >= 0; i--) {
    		shortPath.add(backPath.get(i));}
    	System.out.println("Path Details: " + shortPath);
        return shortPath;
    }

    private Coordinate getShortDist(List<Coordinate> toVisit)
    {
    	Coordinate curr = toVisit.get(0);
    	for(Coordinate coord : toVisit) {
    		if(totalWeight[coord.getRow()][coord.getColumn()]<totalWeight[curr.getRow()][curr.getColumn()])
    			curr=coord;}
    	return curr;
    }
    
    private List<Coordinate> getNeigh(Coordinate coord)
    {
    	List<Coordinate> neigh = new ArrayList<Coordinate>();
    	
    	int col = coord.getColumn();
    	int row = coord.getRow();
    	
    	//Add Upwards
    	if(row>0 || dispMap.isPassable(row-1,col) && dispMap.isIn(row-1, col)) {
    		neigh.add(dispMap.cells[row-1][col]);
    		}
    	
    	
    	//Add Downwards
    	if(row<dispMap.sizeR-1 || dispMap.isPassable(row+1,col) && dispMap.isIn(row+1, col)) {
    		neigh.add(dispMap.cells[row+1][col]);
    		}
    	
    	//Add Left
    	if(col>0 || dispMap.isPassable(row,col-1) && dispMap.isIn(row, col-1)) {
    		neigh.add(dispMap.cells[row][col-1]);
    		}
    	
    	//Add Right
    	if(col<dispMap.sizeC-1 || dispMap.isPassable(row,col+1) && dispMap.isIn(row, col+1)) {
    		neigh.add(dispMap.cells[row][col+1]);
    		}
    	
    	for(int i = 0; i < neigh.size(); ++i) {
    		//System.out.println("Explored Coordinate List: " + neigh.get(i));
    		coordsExplored++;
    	}
    	return neigh;
    }
    
    private List<Coordinate> getToVisit(List<Coordinate> toVisit, List<Coordinate> coords)
    {    	
    	for(int i = 0; i < coords.size(); ++i)
    	{
    		Coordinate cVisit = coords.get(i);
    		if(toVisit.contains(cVisit)) {
    			visitCoord.add(cVisit);}
    	}
    	return visitCoord;
    }

    private List<Coordinate> notVisit()
	{
    	for(int r = 0; r<dispMap.sizeR; ++r) {
    		for(int c = 0; c<dispMap.sizeC; ++c)
    		{
    			Coordinate coord=dispMap.cells[r][c];
    			if(dispMap.isPassable(r, c) && dispMap.isIn(r, c))
    				toVisit.add(coord);
    		}
    	}
   		return toVisit;
	}

    
    public int coordinatesExplored() 
    {
        return coordsExplored;
    }
    
    private void getDetails(PathMap map) 
    {
    	Coordinate srcCoord = dispMap.originCells.get(0);
    	Coordinate destCoord = dispMap.destCells.get(0);
    	
    	System.out.println("Source Coordinate: " + srcCoord);
    	int temp;
    	temp = dispMap.waypointCells.size();
    	
    	if(dispMap.waypointCells.size()!=0) {
	    	for (int i = 0; i < temp; i++) 
	    	{
	    		Coordinate wayCoord = dispMap.waypointCells.get(i);
	    		System.out.println("Waypoint Coordinate: "+ i + " " + wayCoord);
	    	}
    	}
    	System.out.println("Destination Coordinate: " + destCoord);
    }
    
    private void setCost(boolean setZero)
    {
    	
    	while(totalWeight == null) {
    		totalWeight = new int[dispMap.sizeR][dispMap.sizeC];
	    	for(int r=0;r<dispMap.sizeR;++r) {
	    		for(int c=0;c<dispMap.sizeC;++c) {
	    			totalWeight[r][c]=Integer.MAX_VALUE;
	    		}
	    	}
	    	
	    	if(setZero == true) {
	    		for(Coordinate coord : dispMap.originCells) {
	    			totalWeight[coord.getRow()][coord.getColumn()] = 0;
	    		}
	    	}
	    	
	    	if(prevCoord == null) {
	    		prevCoord = new Coordinate[dispMap.sizeR][dispMap.sizeC];
		    	for(int r=0;r<dispMap.sizeR;++r) {
		    		for(int c=0;c<dispMap.sizeC;++c)
		    			prevCoord[r][c]=null;
		    	}
	    	}	
    	}
    }
     
    private int getCost(List<Coordinate> path)
    {
    	int cost = 0;
    	for(Coordinate coord: path) {
    		cost+=dispMap.cells[coord.getRow()][coord.getColumn()].getTerrainCost();
    	}
    	return cost;
    }

}

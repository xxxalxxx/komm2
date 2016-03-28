package tk.melnichuk.kommunalchik.Models;

/**
 * Created by al on 26.03.16.
 */
public class BaseBillModel {

    //write to DataHolder from db
    void getFromDb(){}
    void getMainTableFromDb(){}
    void getSegmentsFromDb(){}

    //write from DataHolder to db
    void writeToDb(){}
    void writeMainTableToDb(){}
    void writeSegmentsToDb(){}

    void getFromView(){}
    void writeToView(){}

    void calc(){}
    void calcMainTable(){}
    void calcSegment(){}
    void calcSegments(){}

    void addSegment(){}
    void addSegments(){}

    void removeSegment(){}

    //getExcelArrayFromDataManager
    void getExcelArray(){}
}

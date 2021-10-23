package GrammarAnalysis;

import Enum.DataType;
import Enum.DimType;
public class FParam {
    String name;
    DataType dataType;
    DimType dimType;

    public FParam(String name, DataType dataType, DimType dimType) {
        this.name = name;
        this.dataType = dataType;
        this.dimType = dimType;
    }

    public String getName() {
        return name;
    }

    public DataType getDataType() {
        return dataType;
    }

    public DimType getDimType() {
        return dimType;
    }
}

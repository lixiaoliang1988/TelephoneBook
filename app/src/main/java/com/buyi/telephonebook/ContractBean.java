package com.buyi.telephonebook;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dell on 2018/12/21.
 */

public class ContractBean implements Serializable{
    public String name;
    public List<String> phones;

    public ContractBean() {
        phones = new ArrayList<>();
    }
}

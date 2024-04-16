package com.d2d.dto;



public class CrudDTO {

    private MultipleRecordDTO multipleRecord ;

    private DeleteDTO delete ;

    private Boolean singleRecord = false;

    private Boolean create = false;

    public MultipleRecordDTO getMultipleRecord() {
        if(multipleRecord==null){
            multipleRecord=new MultipleRecordDTO();
        }
        return multipleRecord;
    }

    public void setMultipleRecord(MultipleRecordDTO multipleRecord) {
        this.multipleRecord = multipleRecord;
    }

    public DeleteDTO getDelete() {
        if (delete==null){
            delete=new DeleteDTO();
        }
        return delete;
    }

    public void setDelete(DeleteDTO delete) {
        this.delete = delete;
    }

    public Boolean getSingleRecord() {
        return singleRecord;
    }

    public void setSingleRecord(Boolean singleRecord) {
        this.singleRecord = singleRecord;
    }

    public Boolean getCreate() {
        return create;
    }

    public void setCreate(Boolean create) {
        this.create = create;
    }
}

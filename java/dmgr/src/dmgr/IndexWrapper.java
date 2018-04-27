package dmgr;

public class IndexWrapper {

    public Long docid;
    public Index index;

    public IndexWrapper(Long docid, Index idx) {
        this.docid = docid;
        this.index = idx;
    }
}

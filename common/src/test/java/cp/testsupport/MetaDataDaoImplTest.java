package cp.testsupport;

import cp.metadata.MetadataDao;
import cp.metadata.MetadataDaoImpl;
import cp.metadata.MetadataSqlSessionBuilder;
import org.apache.ibatis.session.SqlSessionFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import testUtil.MetadataTestDataSource;

import java.io.IOException;

import static org.assertj.core.api.StrictAssertions.assertThat;

/**
 * Created by shengli on 3/13/16.
 */
public class MetaDataDaoImplTest {

    private SqlSessionFactory sqlSessionFactory;
    private MetadataDao metadataDao;

    @BeforeClass
    private void setup() throws IOException {
        MetadataTestDataSource dataSource = new MetadataTestDataSource();
        dataSource.runSqlFromResource("/dbase/metadata-schema.sql");

        sqlSessionFactory = MetadataSqlSessionBuilder.createFor(dataSource, "unittest");
        metadataDao = new MetadataDaoImpl(sqlSessionFactory, dataSource);
    }

    @Test (enabled = false)
    public void testGetUserId(){
        Long userId1 = metadataDao.getNextUserId();
        assertThat(userId1).isEqualTo(1L);

        Long userId2 = metadataDao.getNextUserId();
        assertThat(userId2).isEqualTo(2L);

        Long userId3 = metadataDao.getNextUserId();
        assertThat(userId3).isEqualTo(3L);
    }
}

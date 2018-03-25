package cp.metadata;

import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MetadataDaoImpl extends AbstractMybatisDao implements MetadataDao {
    private static final Logger log = LoggerFactory.getLogger(MetadataDaoImpl.class);

    private final DataSource dataSource;

    public MetadataDaoImpl(SqlSessionFactory sqlSessionFactory, DataSource dataSource) {
        super(sqlSessionFactory);
        this.dataSource = dataSource;
    }

    @Override
    public long getNextUserId() {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("select nextval('user_id_sequence')")) {
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getLong(1);
            } else {
                throw new IllegalStateException("Unable to retrieve location id sequence");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving location id sequence", e);
        }
    }
}

package com.codewithpcodes.cardiag.config;

import com.pgvector.PGvector;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.*;

public class PGVectorType implements UserType<PGvector> {
    @Override
    public int getSqlType() {
        return Types.OTHER;
    }

    @Override
    public Class<PGvector> returnedClass() {
        return PGvector.class;
    }

    @Override
    public boolean equals(PGvector x, PGvector y) {
        if (x == null) return y == null;
        return x.toString().equals(y.toString());
    }

    @Override
    public int hashCode(PGvector x) {
        return x == null ? 0 : x.toString().hashCode();
    }

    @Override
    public PGvector nullSafeGet(
            ResultSet rs,
            int position,
            SharedSessionContractImplementor session,
            Object owner
    ) throws SQLException {
        Object value = rs.getObject(position);
        if (value == null) return null;
        try {
            return new PGvector(value.toString());
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public void nullSafeSet(
            PreparedStatement st, 
            PGvector value, 
            int index, 
            SharedSessionContractImplementor session
    ) throws SQLException {
        if (value == null) {
            st.setNull(index, Types.OTHER);
        } else {
            st.setObject(index, value, Types.OTHER);
        }
    }

    @Override
    public PGvector deepCopy(PGvector value) {
        if (value == null) return null;
        try {
            return new PGvector(value.toString());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(PGvector value) {
        return value == null ? null : value.toString();
    }

    @Override
    public PGvector assemble(Serializable cached, Object owner) {
        if (cached == null) return null;
        try {
            return new PGvector((String) cached);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

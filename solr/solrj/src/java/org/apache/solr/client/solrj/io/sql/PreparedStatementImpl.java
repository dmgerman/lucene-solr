begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj.io.sql
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|sql
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|math
operator|.
name|BigDecimal
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Array
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Blob
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Clob
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|NClob
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|ParameterMetaData
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|PreparedStatement
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Ref
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|ResultSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|ResultSetMetaData
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|RowId
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|SQLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|SQLXML
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Time
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Timestamp
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Calendar
import|;
end_import

begin_class
DECL|class|PreparedStatementImpl
class|class
name|PreparedStatementImpl
extends|extends
name|StatementImpl
implements|implements
name|PreparedStatement
block|{
DECL|field|sql
specifier|private
specifier|final
name|String
name|sql
decl_stmt|;
DECL|method|PreparedStatementImpl
name|PreparedStatementImpl
parameter_list|(
name|ConnectionImpl
name|connection
parameter_list|,
name|String
name|sql
parameter_list|)
block|{
name|super
argument_list|(
name|connection
argument_list|)
expr_stmt|;
name|this
operator|.
name|sql
operator|=
name|sql
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|executeQuery
specifier|public
name|ResultSet
name|executeQuery
parameter_list|()
throws|throws
name|SQLException
block|{
return|return
name|super
operator|.
name|executeQuery
argument_list|(
name|this
operator|.
name|sql
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|executeUpdate
specifier|public
name|int
name|executeUpdate
parameter_list|()
throws|throws
name|SQLException
block|{
return|return
name|super
operator|.
name|executeUpdate
argument_list|(
name|this
operator|.
name|sql
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|execute
specifier|public
name|boolean
name|execute
parameter_list|()
throws|throws
name|SQLException
block|{
return|return
name|super
operator|.
name|execute
argument_list|(
name|this
operator|.
name|sql
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|clearParameters
specifier|public
name|void
name|clearParameters
parameter_list|()
throws|throws
name|SQLException
block|{    }
annotation|@
name|Override
DECL|method|getMetaData
specifier|public
name|ResultSetMetaData
name|getMetaData
parameter_list|()
throws|throws
name|SQLException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|getParameterMetaData
specifier|public
name|ParameterMetaData
name|getParameterMetaData
parameter_list|()
throws|throws
name|SQLException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|addBatch
specifier|public
name|void
name|addBatch
parameter_list|()
throws|throws
name|SQLException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|setNull
specifier|public
name|void
name|setNull
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|int
name|sqlType
parameter_list|)
throws|throws
name|SQLException
block|{    }
annotation|@
name|Override
DECL|method|setBoolean
specifier|public
name|void
name|setBoolean
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|boolean
name|x
parameter_list|)
throws|throws
name|SQLException
block|{    }
annotation|@
name|Override
DECL|method|setByte
specifier|public
name|void
name|setByte
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|byte
name|x
parameter_list|)
throws|throws
name|SQLException
block|{    }
annotation|@
name|Override
DECL|method|setShort
specifier|public
name|void
name|setShort
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|short
name|x
parameter_list|)
throws|throws
name|SQLException
block|{    }
annotation|@
name|Override
DECL|method|setInt
specifier|public
name|void
name|setInt
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|int
name|x
parameter_list|)
throws|throws
name|SQLException
block|{    }
annotation|@
name|Override
DECL|method|setLong
specifier|public
name|void
name|setLong
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|long
name|x
parameter_list|)
throws|throws
name|SQLException
block|{    }
annotation|@
name|Override
DECL|method|setFloat
specifier|public
name|void
name|setFloat
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|float
name|x
parameter_list|)
throws|throws
name|SQLException
block|{    }
annotation|@
name|Override
DECL|method|setDouble
specifier|public
name|void
name|setDouble
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|double
name|x
parameter_list|)
throws|throws
name|SQLException
block|{    }
annotation|@
name|Override
DECL|method|setBigDecimal
specifier|public
name|void
name|setBigDecimal
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|BigDecimal
name|x
parameter_list|)
throws|throws
name|SQLException
block|{    }
annotation|@
name|Override
DECL|method|setString
specifier|public
name|void
name|setString
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|String
name|x
parameter_list|)
throws|throws
name|SQLException
block|{    }
annotation|@
name|Override
DECL|method|setBytes
specifier|public
name|void
name|setBytes
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|byte
index|[]
name|x
parameter_list|)
throws|throws
name|SQLException
block|{    }
annotation|@
name|Override
DECL|method|setDate
specifier|public
name|void
name|setDate
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|Date
name|x
parameter_list|)
throws|throws
name|SQLException
block|{    }
annotation|@
name|Override
DECL|method|setTime
specifier|public
name|void
name|setTime
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|Time
name|x
parameter_list|)
throws|throws
name|SQLException
block|{    }
annotation|@
name|Override
DECL|method|setTimestamp
specifier|public
name|void
name|setTimestamp
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|Timestamp
name|x
parameter_list|)
throws|throws
name|SQLException
block|{    }
annotation|@
name|Override
DECL|method|setAsciiStream
specifier|public
name|void
name|setAsciiStream
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|InputStream
name|x
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|SQLException
block|{    }
annotation|@
name|Override
DECL|method|setUnicodeStream
specifier|public
name|void
name|setUnicodeStream
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|InputStream
name|x
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|SQLException
block|{    }
annotation|@
name|Override
DECL|method|setBinaryStream
specifier|public
name|void
name|setBinaryStream
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|InputStream
name|x
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|SQLException
block|{    }
annotation|@
name|Override
DECL|method|setObject
specifier|public
name|void
name|setObject
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|Object
name|x
parameter_list|,
name|int
name|targetSqlType
parameter_list|)
throws|throws
name|SQLException
block|{    }
annotation|@
name|Override
DECL|method|setObject
specifier|public
name|void
name|setObject
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|Object
name|x
parameter_list|)
throws|throws
name|SQLException
block|{    }
annotation|@
name|Override
DECL|method|setCharacterStream
specifier|public
name|void
name|setCharacterStream
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|Reader
name|reader
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|SQLException
block|{    }
annotation|@
name|Override
DECL|method|setRef
specifier|public
name|void
name|setRef
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|Ref
name|x
parameter_list|)
throws|throws
name|SQLException
block|{    }
annotation|@
name|Override
DECL|method|setBlob
specifier|public
name|void
name|setBlob
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|Blob
name|x
parameter_list|)
throws|throws
name|SQLException
block|{    }
annotation|@
name|Override
DECL|method|setClob
specifier|public
name|void
name|setClob
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|Clob
name|x
parameter_list|)
throws|throws
name|SQLException
block|{    }
annotation|@
name|Override
DECL|method|setArray
specifier|public
name|void
name|setArray
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|Array
name|x
parameter_list|)
throws|throws
name|SQLException
block|{    }
annotation|@
name|Override
DECL|method|setDate
specifier|public
name|void
name|setDate
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|Date
name|x
parameter_list|,
name|Calendar
name|cal
parameter_list|)
throws|throws
name|SQLException
block|{    }
annotation|@
name|Override
DECL|method|setTime
specifier|public
name|void
name|setTime
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|Time
name|x
parameter_list|,
name|Calendar
name|cal
parameter_list|)
throws|throws
name|SQLException
block|{    }
annotation|@
name|Override
DECL|method|setTimestamp
specifier|public
name|void
name|setTimestamp
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|Timestamp
name|x
parameter_list|,
name|Calendar
name|cal
parameter_list|)
throws|throws
name|SQLException
block|{    }
annotation|@
name|Override
DECL|method|setNull
specifier|public
name|void
name|setNull
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|int
name|sqlType
parameter_list|,
name|String
name|typeName
parameter_list|)
throws|throws
name|SQLException
block|{    }
annotation|@
name|Override
DECL|method|setURL
specifier|public
name|void
name|setURL
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|URL
name|x
parameter_list|)
throws|throws
name|SQLException
block|{    }
annotation|@
name|Override
DECL|method|setRowId
specifier|public
name|void
name|setRowId
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|RowId
name|x
parameter_list|)
throws|throws
name|SQLException
block|{    }
annotation|@
name|Override
DECL|method|setNString
specifier|public
name|void
name|setNString
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|SQLException
block|{    }
annotation|@
name|Override
DECL|method|setNCharacterStream
specifier|public
name|void
name|setNCharacterStream
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|Reader
name|value
parameter_list|,
name|long
name|length
parameter_list|)
throws|throws
name|SQLException
block|{    }
annotation|@
name|Override
DECL|method|setNClob
specifier|public
name|void
name|setNClob
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|NClob
name|value
parameter_list|)
throws|throws
name|SQLException
block|{    }
annotation|@
name|Override
DECL|method|setClob
specifier|public
name|void
name|setClob
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|Reader
name|reader
parameter_list|,
name|long
name|length
parameter_list|)
throws|throws
name|SQLException
block|{    }
annotation|@
name|Override
DECL|method|setBlob
specifier|public
name|void
name|setBlob
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|InputStream
name|inputStream
parameter_list|,
name|long
name|length
parameter_list|)
throws|throws
name|SQLException
block|{    }
annotation|@
name|Override
DECL|method|setNClob
specifier|public
name|void
name|setNClob
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|Reader
name|reader
parameter_list|,
name|long
name|length
parameter_list|)
throws|throws
name|SQLException
block|{    }
annotation|@
name|Override
DECL|method|setSQLXML
specifier|public
name|void
name|setSQLXML
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|SQLXML
name|xmlObject
parameter_list|)
throws|throws
name|SQLException
block|{    }
annotation|@
name|Override
DECL|method|setObject
specifier|public
name|void
name|setObject
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|Object
name|x
parameter_list|,
name|int
name|targetSqlType
parameter_list|,
name|int
name|scaleOrLength
parameter_list|)
throws|throws
name|SQLException
block|{    }
annotation|@
name|Override
DECL|method|setAsciiStream
specifier|public
name|void
name|setAsciiStream
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|InputStream
name|x
parameter_list|,
name|long
name|length
parameter_list|)
throws|throws
name|SQLException
block|{    }
annotation|@
name|Override
DECL|method|setBinaryStream
specifier|public
name|void
name|setBinaryStream
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|InputStream
name|x
parameter_list|,
name|long
name|length
parameter_list|)
throws|throws
name|SQLException
block|{    }
annotation|@
name|Override
DECL|method|setCharacterStream
specifier|public
name|void
name|setCharacterStream
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|Reader
name|reader
parameter_list|,
name|long
name|length
parameter_list|)
throws|throws
name|SQLException
block|{    }
annotation|@
name|Override
DECL|method|setAsciiStream
specifier|public
name|void
name|setAsciiStream
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|InputStream
name|x
parameter_list|)
throws|throws
name|SQLException
block|{    }
annotation|@
name|Override
DECL|method|setBinaryStream
specifier|public
name|void
name|setBinaryStream
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|InputStream
name|x
parameter_list|)
throws|throws
name|SQLException
block|{    }
annotation|@
name|Override
DECL|method|setCharacterStream
specifier|public
name|void
name|setCharacterStream
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|Reader
name|reader
parameter_list|)
throws|throws
name|SQLException
block|{    }
annotation|@
name|Override
DECL|method|setNCharacterStream
specifier|public
name|void
name|setNCharacterStream
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|Reader
name|value
parameter_list|)
throws|throws
name|SQLException
block|{    }
annotation|@
name|Override
DECL|method|setClob
specifier|public
name|void
name|setClob
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|Reader
name|reader
parameter_list|)
throws|throws
name|SQLException
block|{    }
annotation|@
name|Override
DECL|method|setBlob
specifier|public
name|void
name|setBlob
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|InputStream
name|inputStream
parameter_list|)
throws|throws
name|SQLException
block|{    }
annotation|@
name|Override
DECL|method|setNClob
specifier|public
name|void
name|setNClob
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|Reader
name|reader
parameter_list|)
throws|throws
name|SQLException
block|{    }
comment|// Methods below cannot be called from a PreparedStatement based on JDBC spec
annotation|@
name|Override
DECL|method|executeQuery
specifier|public
name|ResultSet
name|executeQuery
parameter_list|(
name|String
name|sql
parameter_list|)
throws|throws
name|SQLException
block|{
throw|throw
operator|new
name|SQLException
argument_list|(
literal|"Cannot be called from PreparedStatement"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|executeUpdate
specifier|public
name|int
name|executeUpdate
parameter_list|(
name|String
name|sql
parameter_list|)
throws|throws
name|SQLException
block|{
throw|throw
operator|new
name|SQLException
argument_list|(
literal|"Cannot be called from PreparedStatement"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|execute
specifier|public
name|boolean
name|execute
parameter_list|(
name|String
name|sql
parameter_list|)
throws|throws
name|SQLException
block|{
throw|throw
operator|new
name|SQLException
argument_list|(
literal|"Cannot be called from PreparedStatement"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|addBatch
specifier|public
name|void
name|addBatch
parameter_list|(
name|String
name|sql
parameter_list|)
throws|throws
name|SQLException
block|{
throw|throw
operator|new
name|SQLException
argument_list|(
literal|"Cannot be called from PreparedStatement"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|executeUpdate
specifier|public
name|int
name|executeUpdate
parameter_list|(
name|String
name|sql
parameter_list|,
name|int
name|autoGeneratedKeys
parameter_list|)
throws|throws
name|SQLException
block|{
throw|throw
operator|new
name|SQLException
argument_list|(
literal|"Cannot be called from PreparedStatement"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|executeUpdate
specifier|public
name|int
name|executeUpdate
parameter_list|(
name|String
name|sql
parameter_list|,
name|int
name|columnIndexes
index|[]
parameter_list|)
throws|throws
name|SQLException
block|{
throw|throw
operator|new
name|SQLException
argument_list|(
literal|"Cannot be called from PreparedStatement"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|executeUpdate
specifier|public
name|int
name|executeUpdate
parameter_list|(
name|String
name|sql
parameter_list|,
name|String
name|columnNames
index|[]
parameter_list|)
throws|throws
name|SQLException
block|{
throw|throw
operator|new
name|SQLException
argument_list|(
literal|"Cannot be called from PreparedStatement"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|execute
specifier|public
name|boolean
name|execute
parameter_list|(
name|String
name|sql
parameter_list|,
name|int
name|autoGeneratedKeys
parameter_list|)
throws|throws
name|SQLException
block|{
throw|throw
operator|new
name|SQLException
argument_list|(
literal|"Cannot be called from PreparedStatement"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|execute
specifier|public
name|boolean
name|execute
parameter_list|(
name|String
name|sql
parameter_list|,
name|int
name|columnIndexes
index|[]
parameter_list|)
throws|throws
name|SQLException
block|{
throw|throw
operator|new
name|SQLException
argument_list|(
literal|"Cannot be called from PreparedStatement"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|execute
specifier|public
name|boolean
name|execute
parameter_list|(
name|String
name|sql
parameter_list|,
name|String
name|columnNames
index|[]
parameter_list|)
throws|throws
name|SQLException
block|{
throw|throw
operator|new
name|SQLException
argument_list|(
literal|"Cannot be called from PreparedStatement"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|executeLargeUpdate
specifier|public
name|long
name|executeLargeUpdate
parameter_list|(
name|String
name|sql
parameter_list|)
throws|throws
name|SQLException
block|{
throw|throw
operator|new
name|SQLException
argument_list|(
literal|"Cannot be called from PreparedStatement"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|executeLargeUpdate
specifier|public
name|long
name|executeLargeUpdate
parameter_list|(
name|String
name|sql
parameter_list|,
name|int
name|autoGeneratedKeys
parameter_list|)
throws|throws
name|SQLException
block|{
throw|throw
operator|new
name|SQLException
argument_list|(
literal|"Cannot be called from PreparedStatement"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|executeLargeUpdate
specifier|public
name|long
name|executeLargeUpdate
parameter_list|(
name|String
name|sql
parameter_list|,
name|int
name|columnIndexes
index|[]
parameter_list|)
throws|throws
name|SQLException
block|{
throw|throw
operator|new
name|SQLException
argument_list|(
literal|"Cannot be called from PreparedStatement"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|executeLargeUpdate
specifier|public
name|long
name|executeLargeUpdate
parameter_list|(
name|String
name|sql
parameter_list|,
name|String
name|columnNames
index|[]
parameter_list|)
throws|throws
name|SQLException
block|{
throw|throw
operator|new
name|SQLException
argument_list|(
literal|"Cannot be called from PreparedStatement"
argument_list|)
throw|;
block|}
block|}
end_class

end_unit


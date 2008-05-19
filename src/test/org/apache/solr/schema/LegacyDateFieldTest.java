begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.schema
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
operator|.
name|DateField
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|DateMathParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Fieldable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TimeZone
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|DateFormat
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_class
DECL|class|LegacyDateFieldTest
specifier|public
class|class
name|LegacyDateFieldTest
extends|extends
name|TestCase
block|{
comment|// if and when this class is removed, make sure to refactor all
comment|// appropriate code to DateFieldTest
DECL|field|UTC
specifier|public
specifier|static
name|TimeZone
name|UTC
init|=
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"UTC"
argument_list|)
decl_stmt|;
DECL|field|f
specifier|protected
name|DateField
name|f
init|=
literal|null
decl_stmt|;
DECL|field|p
specifier|protected
name|DateMathParser
name|p
init|=
literal|null
decl_stmt|;
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|p
operator|=
operator|new
name|DateMathParser
argument_list|(
name|UTC
argument_list|,
name|Locale
operator|.
name|US
argument_list|)
expr_stmt|;
name|f
operator|=
operator|new
name|DateField
argument_list|()
expr_stmt|;
comment|// so test can be run against Solr 1.2...
try|try
block|{
name|Class
name|clazz
init|=
name|Class
operator|.
name|forName
argument_list|(
literal|"org.apache.solr.schema.LegacyDateField"
argument_list|)
decl_stmt|;
name|f
operator|=
operator|(
name|DateField
operator|)
name|clazz
operator|.
name|newInstance
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|ignored
parameter_list|)
block|{
comment|// NOOP
block|}
block|}
DECL|method|assertToI
specifier|public
name|void
name|assertToI
parameter_list|(
name|String
name|expected
parameter_list|,
name|String
name|input
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"Input: "
operator|+
name|input
argument_list|,
name|expected
argument_list|,
name|f
operator|.
name|toInternal
argument_list|(
name|input
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testToInternal
specifier|public
name|void
name|testToInternal
parameter_list|()
throws|throws
name|Exception
block|{
name|assertToI
argument_list|(
literal|"1995-12-31T23:59:59.999"
argument_list|,
literal|"1995-12-31T23:59:59.999Z"
argument_list|)
expr_stmt|;
name|assertToI
argument_list|(
literal|"1995-12-31T23:59:59.99"
argument_list|,
literal|"1995-12-31T23:59:59.99Z"
argument_list|)
expr_stmt|;
name|assertToI
argument_list|(
literal|"1995-12-31T23:59:59.9"
argument_list|,
literal|"1995-12-31T23:59:59.9Z"
argument_list|)
expr_stmt|;
name|assertToI
argument_list|(
literal|"1995-12-31T23:59:59"
argument_list|,
literal|"1995-12-31T23:59:59Z"
argument_list|)
expr_stmt|;
comment|// this is the broken behavior
name|assertToI
argument_list|(
literal|"1995-12-31T23:59:59.9998"
argument_list|,
literal|"1995-12-31T23:59:59.9998Z"
argument_list|)
expr_stmt|;
name|assertToI
argument_list|(
literal|"1995-12-31T23:59:59.9990"
argument_list|,
literal|"1995-12-31T23:59:59.9990Z"
argument_list|)
expr_stmt|;
name|assertToI
argument_list|(
literal|"1995-12-31T23:59:59.990"
argument_list|,
literal|"1995-12-31T23:59:59.990Z"
argument_list|)
expr_stmt|;
name|assertToI
argument_list|(
literal|"1995-12-31T23:59:59.900"
argument_list|,
literal|"1995-12-31T23:59:59.900Z"
argument_list|)
expr_stmt|;
name|assertToI
argument_list|(
literal|"1995-12-31T23:59:59.90"
argument_list|,
literal|"1995-12-31T23:59:59.90Z"
argument_list|)
expr_stmt|;
name|assertToI
argument_list|(
literal|"1995-12-31T23:59:59.000"
argument_list|,
literal|"1995-12-31T23:59:59.000Z"
argument_list|)
expr_stmt|;
name|assertToI
argument_list|(
literal|"1995-12-31T23:59:59.00"
argument_list|,
literal|"1995-12-31T23:59:59.00Z"
argument_list|)
expr_stmt|;
name|assertToI
argument_list|(
literal|"1995-12-31T23:59:59.0"
argument_list|,
literal|"1995-12-31T23:59:59.0Z"
argument_list|)
expr_stmt|;
block|}
DECL|method|assertToI
specifier|public
name|void
name|assertToI
parameter_list|(
name|String
name|expected
parameter_list|,
name|long
name|input
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"Input: "
operator|+
name|input
argument_list|,
name|expected
argument_list|,
name|f
operator|.
name|toInternal
argument_list|(
operator|new
name|Date
argument_list|(
name|input
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testToInternalObj
specifier|public
name|void
name|testToInternalObj
parameter_list|()
throws|throws
name|Exception
block|{
name|assertToI
argument_list|(
literal|"1995-12-31T23:59:59.999"
argument_list|,
literal|820454399999l
argument_list|)
expr_stmt|;
comment|// this is the broken behavior
name|assertToI
argument_list|(
literal|"1995-12-31T23:59:59.990"
argument_list|,
literal|820454399990l
argument_list|)
expr_stmt|;
name|assertToI
argument_list|(
literal|"1995-12-31T23:59:59.900"
argument_list|,
literal|820454399900l
argument_list|)
expr_stmt|;
name|assertToI
argument_list|(
literal|"1995-12-31T23:59:59.000"
argument_list|,
literal|820454399000l
argument_list|)
expr_stmt|;
block|}
DECL|method|assertItoR
specifier|public
name|void
name|assertItoR
parameter_list|(
name|String
name|expected
parameter_list|,
name|String
name|input
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"Input: "
operator|+
name|input
argument_list|,
name|expected
argument_list|,
name|f
operator|.
name|indexedToReadable
argument_list|(
name|input
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testIndexedToReadable
specifier|public
name|void
name|testIndexedToReadable
parameter_list|()
block|{
name|assertItoR
argument_list|(
literal|"1995-12-31T23:59:59.999Z"
argument_list|,
literal|"1995-12-31T23:59:59.999"
argument_list|)
expr_stmt|;
name|assertItoR
argument_list|(
literal|"1995-12-31T23:59:59.99Z"
argument_list|,
literal|"1995-12-31T23:59:59.99"
argument_list|)
expr_stmt|;
name|assertItoR
argument_list|(
literal|"1995-12-31T23:59:59.9Z"
argument_list|,
literal|"1995-12-31T23:59:59.9"
argument_list|)
expr_stmt|;
name|assertItoR
argument_list|(
literal|"1995-12-31T23:59:59Z"
argument_list|,
literal|"1995-12-31T23:59:59"
argument_list|)
expr_stmt|;
block|}
DECL|method|testFormatter
specifier|public
name|void
name|testFormatter
parameter_list|()
block|{
name|DateFormat
name|fmt
init|=
name|f
operator|.
name|getThreadLocalDateFormat
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"1970-01-01T00:00:00.005"
argument_list|,
name|fmt
operator|.
name|format
argument_list|(
operator|new
name|Date
argument_list|(
literal|5
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// all of this is broken behavior
name|assertEquals
argument_list|(
literal|"1970-01-01T00:00:00.000"
argument_list|,
name|fmt
operator|.
name|format
argument_list|(
operator|new
name|Date
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1970-01-01T00:00:00.370"
argument_list|,
name|fmt
operator|.
name|format
argument_list|(
operator|new
name|Date
argument_list|(
literal|370
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1970-01-01T00:00:00.900"
argument_list|,
name|fmt
operator|.
name|format
argument_list|(
operator|new
name|Date
argument_list|(
literal|900
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit


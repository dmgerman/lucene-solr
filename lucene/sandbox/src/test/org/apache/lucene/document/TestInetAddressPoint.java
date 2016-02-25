begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.document
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetAddress
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
name|index
operator|.
name|IndexReader
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
name|index
operator|.
name|RandomIndexWriter
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
name|search
operator|.
name|IndexSearcher
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
name|store
operator|.
name|Directory
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
name|util
operator|.
name|LuceneTestCase
import|;
end_import

begin_comment
comment|/** Simple tests for {@link InetAddressPoint} */
end_comment

begin_class
DECL|class|TestInetAddressPoint
specifier|public
class|class
name|TestInetAddressPoint
extends|extends
name|LuceneTestCase
block|{
comment|/** Add a single address and search for it */
DECL|method|testBasics
specifier|public
name|void
name|testBasics
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|)
decl_stmt|;
comment|// add a doc with an address
name|Document
name|document
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|InetAddress
name|address
init|=
name|InetAddress
operator|.
name|getByName
argument_list|(
literal|"1.2.3.4"
argument_list|)
decl_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|InetAddressPoint
argument_list|(
literal|"field"
argument_list|,
name|address
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
comment|// search and verify we found our doc
name|IndexReader
name|reader
init|=
name|writer
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|searcher
operator|.
name|count
argument_list|(
name|InetAddressPoint
operator|.
name|newExactQuery
argument_list|(
literal|"field"
argument_list|,
name|address
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|searcher
operator|.
name|count
argument_list|(
name|InetAddressPoint
operator|.
name|newPrefixQuery
argument_list|(
literal|"field"
argument_list|,
name|address
argument_list|,
literal|24
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|searcher
operator|.
name|count
argument_list|(
name|InetAddressPoint
operator|.
name|newRangeQuery
argument_list|(
literal|"field"
argument_list|,
name|InetAddress
operator|.
name|getByName
argument_list|(
literal|"1.2.3.3"
argument_list|)
argument_list|,
literal|false
argument_list|,
name|InetAddress
operator|.
name|getByName
argument_list|(
literal|"1.2.3.5"
argument_list|)
argument_list|,
literal|false
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|searcher
operator|.
name|count
argument_list|(
name|InetAddressPoint
operator|.
name|newSetQuery
argument_list|(
literal|"field"
argument_list|,
name|InetAddress
operator|.
name|getByName
argument_list|(
literal|"1.2.3.4"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|searcher
operator|.
name|count
argument_list|(
name|InetAddressPoint
operator|.
name|newSetQuery
argument_list|(
literal|"field"
argument_list|,
name|InetAddress
operator|.
name|getByName
argument_list|(
literal|"1.2.3.3"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|searcher
operator|.
name|count
argument_list|(
name|InetAddressPoint
operator|.
name|newSetQuery
argument_list|(
literal|"field"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** Add a single address and search for it */
DECL|method|testBasicsV6
specifier|public
name|void
name|testBasicsV6
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|)
decl_stmt|;
comment|// add a doc with an address
name|Document
name|document
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|InetAddress
name|address
init|=
name|InetAddress
operator|.
name|getByName
argument_list|(
literal|"fec0::f66d"
argument_list|)
decl_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|InetAddressPoint
argument_list|(
literal|"field"
argument_list|,
name|address
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
comment|// search and verify we found our doc
name|IndexReader
name|reader
init|=
name|writer
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|searcher
operator|.
name|count
argument_list|(
name|InetAddressPoint
operator|.
name|newExactQuery
argument_list|(
literal|"field"
argument_list|,
name|address
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|searcher
operator|.
name|count
argument_list|(
name|InetAddressPoint
operator|.
name|newPrefixQuery
argument_list|(
literal|"field"
argument_list|,
name|address
argument_list|,
literal|64
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|searcher
operator|.
name|count
argument_list|(
name|InetAddressPoint
operator|.
name|newRangeQuery
argument_list|(
literal|"field"
argument_list|,
name|InetAddress
operator|.
name|getByName
argument_list|(
literal|"fec0::f66c"
argument_list|)
argument_list|,
literal|false
argument_list|,
name|InetAddress
operator|.
name|getByName
argument_list|(
literal|"fec0::f66e"
argument_list|)
argument_list|,
literal|false
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testToString
specifier|public
name|void
name|testToString
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|"InetAddressPoint<field:1.2.3.4>"
argument_list|,
operator|new
name|InetAddressPoint
argument_list|(
literal|"field"
argument_list|,
name|InetAddress
operator|.
name|getByName
argument_list|(
literal|"1.2.3.4"
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"InetAddressPoint<field:1.2.3.4>"
argument_list|,
operator|new
name|InetAddressPoint
argument_list|(
literal|"field"
argument_list|,
name|InetAddress
operator|.
name|getByName
argument_list|(
literal|"::FFFF:1.2.3.4"
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"InetAddressPoint<field:[fdc8:57ed:f042:ad1:f66d:4ff:fe90:ce0c]>"
argument_list|,
operator|new
name|InetAddressPoint
argument_list|(
literal|"field"
argument_list|,
name|InetAddress
operator|.
name|getByName
argument_list|(
literal|"fdc8:57ed:f042:0ad1:f66d:4ff:fe90:ce0c"
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"field:[1.2.3.4 TO 1.2.3.4]"
argument_list|,
name|InetAddressPoint
operator|.
name|newExactQuery
argument_list|(
literal|"field"
argument_list|,
name|InetAddress
operator|.
name|getByName
argument_list|(
literal|"1.2.3.4"
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"field:[0:0:0:0:0:0:0:1 TO 0:0:0:0:0:0:0:1]"
argument_list|,
name|InetAddressPoint
operator|.
name|newExactQuery
argument_list|(
literal|"field"
argument_list|,
name|InetAddress
operator|.
name|getByName
argument_list|(
literal|"::1"
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"field:[1.2.3.0 TO 1.2.3.255]"
argument_list|,
name|InetAddressPoint
operator|.
name|newPrefixQuery
argument_list|(
literal|"field"
argument_list|,
name|InetAddress
operator|.
name|getByName
argument_list|(
literal|"1.2.3.4"
argument_list|)
argument_list|,
literal|24
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"field:[fdc8:57ed:f042:ad1:0:0:0:0 TO fdc8:57ed:f042:ad1:ffff:ffff:ffff:ffff]"
argument_list|,
name|InetAddressPoint
operator|.
name|newPrefixQuery
argument_list|(
literal|"field"
argument_list|,
name|InetAddress
operator|.
name|getByName
argument_list|(
literal|"fdc8:57ed:f042:0ad1:f66d:4ff:fe90:ce0c"
argument_list|)
argument_list|,
literal|64
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"field:{fdc8:57ed:f042:ad1:f66d:4ff:fe90:ce0c}"
argument_list|,
name|InetAddressPoint
operator|.
name|newSetQuery
argument_list|(
literal|"field"
argument_list|,
name|InetAddress
operator|.
name|getByName
argument_list|(
literal|"fdc8:57ed:f042:0ad1:f66d:4ff:fe90:ce0c"
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit


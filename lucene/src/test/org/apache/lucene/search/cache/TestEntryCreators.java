begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.cache
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|cache
package|;
end_package

begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Constructor
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|analysis
operator|.
name|MockAnalyzer
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
name|Document
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
name|Field
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
name|FieldCache
operator|.
name|*
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
name|FieldCache
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
name|OpenBitSet
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|*
import|;
end_import

begin_class
DECL|class|TestEntryCreators
specifier|public
class|class
name|TestEntryCreators
extends|extends
name|LuceneTestCase
block|{
DECL|field|reader
specifier|protected
name|IndexReader
name|reader
decl_stmt|;
DECL|field|NUM_DOCS
specifier|private
specifier|static
specifier|final
name|int
name|NUM_DOCS
init|=
name|atLeast
argument_list|(
literal|500
argument_list|)
decl_stmt|;
DECL|field|directory
specifier|private
name|Directory
name|directory
decl_stmt|;
DECL|class|NumberTypeTester
specifier|static
class|class
name|NumberTypeTester
block|{
DECL|field|funcName
name|String
name|funcName
decl_stmt|;
DECL|field|creator
name|Class
argument_list|<
name|?
extends|extends
name|CachedArrayCreator
argument_list|>
name|creator
decl_stmt|;
DECL|field|parser
name|Class
argument_list|<
name|?
extends|extends
name|Parser
argument_list|>
name|parser
decl_stmt|;
DECL|field|field
name|String
name|field
decl_stmt|;
DECL|field|values
name|Number
index|[]
name|values
decl_stmt|;
DECL|method|NumberTypeTester
specifier|public
name|NumberTypeTester
parameter_list|(
name|String
name|f
parameter_list|,
name|String
name|func
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|CachedArrayCreator
argument_list|>
name|creator
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|Parser
argument_list|>
name|parser
parameter_list|)
block|{
name|field
operator|=
name|f
expr_stmt|;
name|funcName
operator|=
name|func
expr_stmt|;
name|this
operator|.
name|creator
operator|=
name|creator
expr_stmt|;
name|this
operator|.
name|parser
operator|=
name|parser
expr_stmt|;
name|values
operator|=
operator|new
name|Number
index|[
name|NUM_DOCS
index|]
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|field
return|;
block|}
block|}
DECL|field|typeTests
specifier|private
name|NumberTypeTester
index|[]
name|typeTests
decl_stmt|;
annotation|@
name|Override
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
name|directory
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|,
name|directory
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|)
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|typeTests
operator|=
operator|new
name|NumberTypeTester
index|[]
block|{
operator|new
name|NumberTypeTester
argument_list|(
literal|"theRandomByte"
argument_list|,
literal|"getBytes"
argument_list|,
name|ByteValuesCreator
operator|.
name|class
argument_list|,
name|ByteParser
operator|.
name|class
argument_list|)
block|,
operator|new
name|NumberTypeTester
argument_list|(
literal|"theRandomShort"
argument_list|,
literal|"getShorts"
argument_list|,
name|ShortValuesCreator
operator|.
name|class
argument_list|,
name|ShortParser
operator|.
name|class
argument_list|)
block|,
operator|new
name|NumberTypeTester
argument_list|(
literal|"theRandomInt"
argument_list|,
literal|"getInts"
argument_list|,
name|IntValuesCreator
operator|.
name|class
argument_list|,
name|IntParser
operator|.
name|class
argument_list|)
block|,
operator|new
name|NumberTypeTester
argument_list|(
literal|"theRandomLong"
argument_list|,
literal|"getLongs"
argument_list|,
name|LongValuesCreator
operator|.
name|class
argument_list|,
name|LongParser
operator|.
name|class
argument_list|)
block|,
operator|new
name|NumberTypeTester
argument_list|(
literal|"theRandomFloat"
argument_list|,
literal|"getFloats"
argument_list|,
name|FloatValuesCreator
operator|.
name|class
argument_list|,
name|FloatParser
operator|.
name|class
argument_list|)
block|,
operator|new
name|NumberTypeTester
argument_list|(
literal|"theRandomDouble"
argument_list|,
literal|"getDoubles"
argument_list|,
name|DoubleValuesCreator
operator|.
name|class
argument_list|,
name|DoubleParser
operator|.
name|class
argument_list|)
block|,     }
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUM_DOCS
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
comment|// Test the valid bits
for|for
control|(
name|NumberTypeTester
name|tester
range|:
name|typeTests
control|)
block|{
if|if
condition|(
name|random
operator|.
name|nextInt
argument_list|(
literal|20
argument_list|)
operator|!=
literal|17
operator|&&
name|i
operator|>
literal|1
condition|)
block|{
name|tester
operator|.
name|values
index|[
name|i
index|]
operator|=
literal|10
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|20
argument_list|)
expr_stmt|;
comment|// get some field overlap
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
name|tester
operator|.
name|field
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|tester
operator|.
name|values
index|[
name|i
index|]
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|NOT_ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|reader
operator|=
name|writer
operator|.
name|getReader
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|testKeys
specifier|public
name|void
name|testKeys
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Check that the keys are unique for different fields
name|EntryKey
name|key_1
init|=
operator|new
name|ByteValuesCreator
argument_list|(
literal|"field1"
argument_list|,
literal|null
argument_list|)
operator|.
name|getCacheKey
argument_list|()
decl_stmt|;
name|EntryKey
name|key_2
init|=
operator|new
name|ByteValuesCreator
argument_list|(
literal|"field2"
argument_list|,
literal|null
argument_list|)
operator|.
name|getCacheKey
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
literal|"different fields should have a different key"
argument_list|,
name|key_1
argument_list|,
name|not
argument_list|(
name|key_2
argument_list|)
argument_list|)
expr_stmt|;
name|key_1
operator|=
operator|new
name|ByteValuesCreator
argument_list|(
literal|"field1"
argument_list|,
literal|null
argument_list|)
operator|.
name|getCacheKey
argument_list|()
expr_stmt|;
name|key_2
operator|=
operator|new
name|ShortValuesCreator
argument_list|(
literal|"field1"
argument_list|,
literal|null
argument_list|)
operator|.
name|getCacheKey
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
literal|"same field different type should have different key"
argument_list|,
name|key_1
argument_list|,
name|not
argument_list|(
name|key_2
argument_list|)
argument_list|)
expr_stmt|;
name|key_1
operator|=
operator|new
name|ByteValuesCreator
argument_list|(
literal|"ff"
argument_list|,
literal|null
argument_list|)
operator|.
name|getCacheKey
argument_list|()
expr_stmt|;
name|key_2
operator|=
operator|new
name|ByteValuesCreator
argument_list|(
literal|"ff"
argument_list|,
literal|null
argument_list|)
operator|.
name|getCacheKey
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
literal|"same args should have same key"
argument_list|,
name|key_1
argument_list|,
name|is
argument_list|(
name|key_2
argument_list|)
argument_list|)
expr_stmt|;
name|key_1
operator|=
operator|new
name|ByteValuesCreator
argument_list|(
literal|"ff"
argument_list|,
literal|null
argument_list|,
name|ByteValuesCreator
operator|.
name|OPTION_CACHE_BITS
operator|^
name|ByteValuesCreator
operator|.
name|OPTION_CACHE_VALUES
argument_list|)
operator|.
name|getCacheKey
argument_list|()
expr_stmt|;
name|key_2
operator|=
operator|new
name|ByteValuesCreator
argument_list|(
literal|"ff"
argument_list|,
literal|null
argument_list|)
operator|.
name|getCacheKey
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
literal|"different options should share same key"
argument_list|,
name|key_1
argument_list|,
name|is
argument_list|(
name|key_2
argument_list|)
argument_list|)
expr_stmt|;
name|key_1
operator|=
operator|new
name|IntValuesCreator
argument_list|(
literal|"ff"
argument_list|,
name|FieldCache
operator|.
name|DEFAULT_INT_PARSER
argument_list|)
operator|.
name|getCacheKey
argument_list|()
expr_stmt|;
name|key_2
operator|=
operator|new
name|IntValuesCreator
argument_list|(
literal|"ff"
argument_list|,
name|FieldCache
operator|.
name|NUMERIC_UTILS_INT_PARSER
argument_list|)
operator|.
name|getCacheKey
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
literal|"diferent parser should have same key"
argument_list|,
name|key_1
argument_list|,
name|is
argument_list|(
name|key_2
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getWithReflection
specifier|private
name|CachedArray
name|getWithReflection
parameter_list|(
name|FieldCache
name|cache
parameter_list|,
name|NumberTypeTester
name|tester
parameter_list|,
name|int
name|flags
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|Method
name|getXXX
init|=
name|cache
operator|.
name|getClass
argument_list|()
operator|.
name|getMethod
argument_list|(
name|tester
operator|.
name|funcName
argument_list|,
name|IndexReader
operator|.
name|class
argument_list|,
name|String
operator|.
name|class
argument_list|,
name|EntryCreator
operator|.
name|class
argument_list|)
decl_stmt|;
name|Constructor
name|constructor
init|=
name|tester
operator|.
name|creator
operator|.
name|getConstructor
argument_list|(
name|String
operator|.
name|class
argument_list|,
name|tester
operator|.
name|parser
argument_list|,
name|Integer
operator|.
name|TYPE
argument_list|)
decl_stmt|;
name|CachedArrayCreator
name|creator
init|=
operator|(
name|CachedArrayCreator
operator|)
name|constructor
operator|.
name|newInstance
argument_list|(
name|tester
operator|.
name|field
argument_list|,
literal|null
argument_list|,
name|flags
argument_list|)
decl_stmt|;
return|return
operator|(
name|CachedArray
operator|)
name|getXXX
operator|.
name|invoke
argument_list|(
name|cache
argument_list|,
name|reader
argument_list|,
name|tester
operator|.
name|field
argument_list|,
name|creator
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Reflection failed"
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
DECL|method|testCachedArrays
specifier|public
name|void
name|testCachedArrays
parameter_list|()
throws|throws
name|IOException
block|{
name|FieldCache
name|cache
init|=
name|FieldCache
operator|.
name|DEFAULT
decl_stmt|;
comment|// Check the Different CachedArray Types
name|CachedArray
name|last
init|=
literal|null
decl_stmt|;
name|CachedArray
name|justbits
init|=
literal|null
decl_stmt|;
name|String
name|field
decl_stmt|;
for|for
control|(
name|NumberTypeTester
name|tester
range|:
name|typeTests
control|)
block|{
name|justbits
operator|=
name|getWithReflection
argument_list|(
name|cache
argument_list|,
name|tester
argument_list|,
name|CachedArrayCreator
operator|.
name|OPTION_CACHE_BITS
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"should not get values : "
operator|+
name|tester
argument_list|,
name|justbits
operator|.
name|getRawArray
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"should get bits : "
operator|+
name|tester
argument_list|,
name|justbits
operator|.
name|valid
argument_list|)
expr_stmt|;
name|last
operator|=
name|getWithReflection
argument_list|(
name|cache
argument_list|,
name|tester
argument_list|,
name|CachedArrayCreator
operator|.
name|CACHE_VALUES_AND_BITS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"should use same cached object : "
operator|+
name|tester
argument_list|,
name|justbits
argument_list|,
name|last
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"Validate=false shoudl not regenerate : "
operator|+
name|tester
argument_list|,
name|justbits
operator|.
name|getRawArray
argument_list|()
argument_list|)
expr_stmt|;
name|last
operator|=
name|getWithReflection
argument_list|(
name|cache
argument_list|,
name|tester
argument_list|,
name|CachedArrayCreator
operator|.
name|CACHE_VALUES_AND_BITS_VALIDATE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"should use same cached object : "
operator|+
name|tester
argument_list|,
name|justbits
argument_list|,
name|last
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Validate=true should add the Array : "
operator|+
name|tester
argument_list|,
name|justbits
operator|.
name|getRawArray
argument_list|()
argument_list|)
expr_stmt|;
name|checkCachedArrayValuesAndBits
argument_list|(
name|tester
argument_list|,
name|last
argument_list|)
expr_stmt|;
block|}
comment|// Now switch the the parser (for the same type) and expect an error
name|cache
operator|.
name|purgeAllCaches
argument_list|()
expr_stmt|;
name|int
name|flags
init|=
name|CachedArrayCreator
operator|.
name|CACHE_VALUES_AND_BITS_VALIDATE
decl_stmt|;
name|field
operator|=
literal|"theRandomInt"
expr_stmt|;
name|last
operator|=
name|cache
operator|.
name|getInts
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
operator|new
name|IntValuesCreator
argument_list|(
name|field
argument_list|,
name|FieldCache
operator|.
name|DEFAULT_INT_PARSER
argument_list|,
name|flags
argument_list|)
argument_list|)
expr_stmt|;
name|checkCachedArrayValuesAndBits
argument_list|(
name|typeTests
index|[
literal|2
index|]
argument_list|,
name|last
argument_list|)
expr_stmt|;
try|try
block|{
name|cache
operator|.
name|getInts
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
operator|new
name|IntValuesCreator
argument_list|(
name|field
argument_list|,
name|FieldCache
operator|.
name|NUMERIC_UTILS_INT_PARSER
argument_list|,
name|flags
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should fail if you ask for the same type with a different parser : "
operator|+
name|field
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{}
comment|// expected
name|field
operator|=
literal|"theRandomLong"
expr_stmt|;
name|last
operator|=
name|cache
operator|.
name|getLongs
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
operator|new
name|LongValuesCreator
argument_list|(
name|field
argument_list|,
name|FieldCache
operator|.
name|DEFAULT_LONG_PARSER
argument_list|,
name|flags
argument_list|)
argument_list|)
expr_stmt|;
name|checkCachedArrayValuesAndBits
argument_list|(
name|typeTests
index|[
literal|3
index|]
argument_list|,
name|last
argument_list|)
expr_stmt|;
try|try
block|{
name|cache
operator|.
name|getLongs
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
operator|new
name|LongValuesCreator
argument_list|(
name|field
argument_list|,
name|FieldCache
operator|.
name|NUMERIC_UTILS_LONG_PARSER
argument_list|,
name|flags
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should fail if you ask for the same type with a different parser : "
operator|+
name|field
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{}
comment|// expected
name|field
operator|=
literal|"theRandomFloat"
expr_stmt|;
name|last
operator|=
name|cache
operator|.
name|getFloats
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
operator|new
name|FloatValuesCreator
argument_list|(
name|field
argument_list|,
name|FieldCache
operator|.
name|DEFAULT_FLOAT_PARSER
argument_list|,
name|flags
argument_list|)
argument_list|)
expr_stmt|;
name|checkCachedArrayValuesAndBits
argument_list|(
name|typeTests
index|[
literal|4
index|]
argument_list|,
name|last
argument_list|)
expr_stmt|;
try|try
block|{
name|cache
operator|.
name|getFloats
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
operator|new
name|FloatValuesCreator
argument_list|(
name|field
argument_list|,
name|FieldCache
operator|.
name|NUMERIC_UTILS_FLOAT_PARSER
argument_list|,
name|flags
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should fail if you ask for the same type with a different parser : "
operator|+
name|field
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{}
comment|// expected
name|field
operator|=
literal|"theRandomDouble"
expr_stmt|;
name|last
operator|=
name|cache
operator|.
name|getDoubles
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
operator|new
name|DoubleValuesCreator
argument_list|(
name|field
argument_list|,
name|FieldCache
operator|.
name|DEFAULT_DOUBLE_PARSER
argument_list|,
name|flags
argument_list|)
argument_list|)
expr_stmt|;
name|checkCachedArrayValuesAndBits
argument_list|(
name|typeTests
index|[
literal|5
index|]
argument_list|,
name|last
argument_list|)
expr_stmt|;
try|try
block|{
name|cache
operator|.
name|getDoubles
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
operator|new
name|DoubleValuesCreator
argument_list|(
name|field
argument_list|,
name|FieldCache
operator|.
name|NUMERIC_UTILS_DOUBLE_PARSER
argument_list|,
name|flags
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should fail if you ask for the same type with a different parser : "
operator|+
name|field
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{}
comment|// expected
block|}
DECL|method|checkCachedArrayValuesAndBits
specifier|private
name|void
name|checkCachedArrayValuesAndBits
parameter_list|(
name|NumberTypeTester
name|tester
parameter_list|,
name|CachedArray
name|cachedVals
parameter_list|)
block|{
comment|//    for( int i=0; i<NUM_DOCS; i++ ) {
comment|//      System.out.println( i + "] "+ tester.values[i] + " :: " + cachedVals.valid.get(i) );
comment|//    }
name|int
name|numDocs
init|=
literal|0
decl_stmt|;
name|Set
argument_list|<
name|Number
argument_list|>
name|distinctTerms
init|=
operator|new
name|HashSet
argument_list|<
name|Number
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUM_DOCS
condition|;
name|i
operator|++
control|)
block|{
name|Number
name|v
init|=
name|tester
operator|.
name|values
index|[
name|i
index|]
decl_stmt|;
name|boolean
name|isValid
init|=
name|cachedVals
operator|.
name|valid
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|!=
literal|null
condition|)
block|{
name|numDocs
operator|++
expr_stmt|;
name|distinctTerms
operator|.
name|add
argument_list|(
name|v
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Valid bit should be true ("
operator|+
name|i
operator|+
literal|"="
operator|+
name|tester
operator|.
name|values
index|[
name|i
index|]
operator|+
literal|") "
operator|+
name|tester
argument_list|,
name|isValid
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertFalse
argument_list|(
literal|"Valid bit should be false ("
operator|+
name|i
operator|+
literal|") "
operator|+
name|tester
argument_list|,
name|isValid
argument_list|)
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
literal|"Cached numTerms does not match : "
operator|+
name|tester
argument_list|,
name|distinctTerms
operator|.
name|size
argument_list|()
argument_list|,
name|cachedVals
operator|.
name|numTerms
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Cached numDocs does not match : "
operator|+
name|tester
argument_list|,
name|numDocs
argument_list|,
name|cachedVals
operator|.
name|numDocs
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Ordinal should match numDocs : "
operator|+
name|tester
argument_list|,
name|numDocs
argument_list|,
operator|(
operator|(
name|OpenBitSet
operator|)
name|cachedVals
operator|.
name|valid
operator|)
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit


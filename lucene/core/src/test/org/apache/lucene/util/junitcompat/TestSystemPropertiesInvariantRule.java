begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util.junitcompat
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|junitcompat
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
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
name|junit
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|JUnitCore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|Result
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|notification
operator|.
name|Failure
import|;
end_import

begin_class
DECL|class|TestSystemPropertiesInvariantRule
specifier|public
class|class
name|TestSystemPropertiesInvariantRule
block|{
DECL|field|PROP_KEY1
specifier|public
specifier|static
specifier|final
name|String
name|PROP_KEY1
init|=
literal|"new-property-1"
decl_stmt|;
DECL|field|VALUE1
specifier|public
specifier|static
specifier|final
name|String
name|VALUE1
init|=
literal|"new-value-1"
decl_stmt|;
DECL|class|Base
specifier|public
specifier|static
class|class
name|Base
extends|extends
name|LuceneTestCase
block|{
DECL|method|testEmpty
specifier|public
name|void
name|testEmpty
parameter_list|()
block|{}
block|}
DECL|class|InBeforeClass
specifier|public
specifier|static
class|class
name|InBeforeClass
extends|extends
name|Base
block|{
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
block|{
name|System
operator|.
name|setProperty
argument_list|(
name|PROP_KEY1
argument_list|,
name|VALUE1
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|InAfterClass
specifier|public
specifier|static
class|class
name|InAfterClass
extends|extends
name|Base
block|{
annotation|@
name|AfterClass
DECL|method|afterClass
specifier|public
specifier|static
name|void
name|afterClass
parameter_list|()
block|{
name|System
operator|.
name|setProperty
argument_list|(
name|PROP_KEY1
argument_list|,
name|VALUE1
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|InTestMethod
specifier|public
specifier|static
class|class
name|InTestMethod
extends|extends
name|Base
block|{
DECL|method|testMethod1
specifier|public
name|void
name|testMethod1
parameter_list|()
block|{
if|if
condition|(
name|System
operator|.
name|getProperty
argument_list|(
name|PROP_KEY1
argument_list|)
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Shouldn't be here."
argument_list|)
throw|;
block|}
name|System
operator|.
name|setProperty
argument_list|(
name|PROP_KEY1
argument_list|,
name|VALUE1
argument_list|)
expr_stmt|;
block|}
DECL|method|testMethod2
specifier|public
name|void
name|testMethod2
parameter_list|()
block|{
name|testMethod1
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|NonStringProperties
specifier|public
specifier|static
class|class
name|NonStringProperties
extends|extends
name|Base
block|{
DECL|method|testMethod1
specifier|public
name|void
name|testMethod1
parameter_list|()
block|{
if|if
condition|(
name|System
operator|.
name|getProperties
argument_list|()
operator|.
name|get
argument_list|(
name|PROP_KEY1
argument_list|)
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Will pass."
argument_list|)
throw|;
block|}
name|Properties
name|properties
init|=
name|System
operator|.
name|getProperties
argument_list|()
decl_stmt|;
name|properties
operator|.
name|put
argument_list|(
name|PROP_KEY1
argument_list|,
operator|new
name|Object
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|System
operator|.
name|getProperties
argument_list|()
operator|.
name|get
argument_list|(
name|PROP_KEY1
argument_list|)
operator|!=
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|testMethod2
specifier|public
name|void
name|testMethod2
parameter_list|()
block|{
name|testMethod1
argument_list|()
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|cleanup
specifier|public
specifier|static
name|void
name|cleanup
parameter_list|()
block|{
name|System
operator|.
name|getProperties
argument_list|()
operator|.
name|remove
argument_list|(
name|PROP_KEY1
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testRuleInvariantBeforeClass
specifier|public
name|void
name|testRuleInvariantBeforeClass
parameter_list|()
block|{
name|Result
name|runClasses
init|=
name|JUnitCore
operator|.
name|runClasses
argument_list|(
name|InBeforeClass
operator|.
name|class
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|runClasses
operator|.
name|getFailureCount
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|runClasses
operator|.
name|getFailures
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
name|PROP_KEY1
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
name|PROP_KEY1
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRuleInvariantAfterClass
specifier|public
name|void
name|testRuleInvariantAfterClass
parameter_list|()
block|{
name|Result
name|runClasses
init|=
name|JUnitCore
operator|.
name|runClasses
argument_list|(
name|InAfterClass
operator|.
name|class
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|runClasses
operator|.
name|getFailureCount
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|runClasses
operator|.
name|getFailures
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
name|PROP_KEY1
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
name|PROP_KEY1
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRuleInvariantInTestMethod
specifier|public
name|void
name|testRuleInvariantInTestMethod
parameter_list|()
block|{
name|Result
name|runClasses
init|=
name|JUnitCore
operator|.
name|runClasses
argument_list|(
name|InTestMethod
operator|.
name|class
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|runClasses
operator|.
name|getFailureCount
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Failure
name|f
range|:
name|runClasses
operator|.
name|getFailures
argument_list|()
control|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|f
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
name|PROP_KEY1
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Assert
operator|.
name|assertNull
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
name|PROP_KEY1
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNonStringProperties
specifier|public
name|void
name|testNonStringProperties
parameter_list|()
block|{
name|Result
name|runClasses
init|=
name|JUnitCore
operator|.
name|runClasses
argument_list|(
name|NonStringProperties
operator|.
name|class
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|runClasses
operator|.
name|getFailureCount
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|runClasses
operator|.
name|getFailures
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Will pass"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|runClasses
operator|.
name|getRunCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit


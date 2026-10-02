package com.inventory.control.system.adapters.out;

import com.inventory.control.system.adapters.in.web.mapper.CategoryMapper;
import com.inventory.control.system.adapters.out.database.mongodb.documents.CategoryDocument;
import com.inventory.control.system.adapters.out.database.mongodb.repository.MongoCategoryRepository;
import com.inventory.control.system.adapters.out.exception.PersistenceException;
import com.inventory.control.system.domain.model.Category;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataRetrievalFailureException;

import java.util.List;
import java.util.Optional;

import static com.inventory.control.system.mocks.CategoryMockFactory.createCategoryWithId;
import static com.inventory.control.system.mocks.CategoryDocumentMockFactory.createCategoryDocumentWithId;
import static com.inventory.control.system.mocks.CategoryDocumentMockFactory.createCategoryDocumentWithoutId;
import static com.inventory.control.system.mocks.CategoryDocumentMockFactory.createCategoryDocument;
import static com.inventory.control.system.mocks.CategoryDocumentMockFactory.DEFAULT_CATEGORY_ID;
import static com.inventory.control.system.mocks.CategoryDocumentMockFactory.DEFAULT_CATEGORY_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryDatabaseAdapterTest {

    @Mock
    private CategoryMapper mapper;

    @Mock
    private MongoCategoryRepository repository;

    private MeterRegistry meterRegistry;
    private CategoryDatabaseAdapter adapter;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        adapter = new CategoryDatabaseAdapter(mapper, repository, meterRegistry);
    }

    @Test
    void shouldSaveCategorySuccessfully() {
        Category categoryDomain = createCategoryWithId();
        CategoryDocument categoryDocument = createCategoryDocumentWithId();

        when(mapper.toDocument(categoryDomain)).thenReturn(categoryDocument);
        when(repository.save(categoryDocument)).thenReturn(categoryDocument);
        when(mapper.toDomain(categoryDocument)).thenReturn(categoryDomain);

        Category result = adapter.saveCategory(categoryDomain);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(DEFAULT_CATEGORY_ID);
        assertThat(result.getName()).isEqualTo(DEFAULT_CATEGORY_NAME);

        verify(mapper).toDocument(categoryDomain);
        verify(repository).save(categoryDocument);
        verify(mapper).toDomain(categoryDocument);

        assertThat(meterRegistry.find("db.mongodb.category.query.time").timer()).isNotNull();
    }

    @Test
    void shouldThrowPersistenceExceptionWhenSaveFails() {
        Category categoryDomain = createCategoryWithId();
        CategoryDocument categoryDocument = createCategoryDocumentWithId();

        when(mapper.toDocument(categoryDomain)).thenReturn(categoryDocument);
        when(repository.save(categoryDocument)).thenThrow(new DataRetrievalFailureException("Falha no banco"));

        assertThatThrownBy(() -> adapter.saveCategory(categoryDomain))
                .isInstanceOf(PersistenceException.class)
                .hasMessage("Erro ao salvar categoria no banco de dados MongoDB.")
                .hasCauseInstanceOf(DataRetrievalFailureException.class);

        double errorCount = meterRegistry.counter("db.mongodb.category.errors",
                "layer", "adapter",
                "operation", "save",
                "exception", "DataRetrievalFailureException",
                "db", "mongodb").count();

        assertThat(errorCount).isEqualTo(1.0);
    }

    @Test
    void shouldUpdateCategorySuccessfully() {
        Category categoryDomain = createCategoryWithId();
        CategoryDocument categoryDocument = createCategoryDocumentWithId();

        when(mapper.toDocument(categoryDomain)).thenReturn(categoryDocument);
        when(repository.save(categoryDocument)).thenReturn(categoryDocument);
        when(mapper.toDomain(categoryDocument)).thenReturn(categoryDomain);

        Category result = adapter.updateCategory(categoryDomain);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(DEFAULT_CATEGORY_ID);

        verify(mapper).toDocument(categoryDomain);
        verify(repository).save(categoryDocument);
        verify(mapper).toDomain(categoryDocument);
    }

    @Test
    void shouldThrowPersistenceExceptionWhenUpdateFails() {
        Category categoryDomain = createCategoryWithId();
        CategoryDocument categoryDocument = createCategoryDocumentWithId();

        when(mapper.toDocument(categoryDomain)).thenReturn(categoryDocument);
        when(repository.save(categoryDocument)).thenThrow(new DataRetrievalFailureException("Falha de conexão"));

        assertThatThrownBy(() -> adapter.updateCategory(categoryDomain))
                .isInstanceOf(PersistenceException.class)
                .hasMessage("Erro ao atualizar categoria no banco de dados MongoDB.");
    }

    @Test
    void shouldReturnTrueWhenCategoryExistsById() {
        when(repository.existsById(DEFAULT_CATEGORY_ID)).thenReturn(true);

        boolean exists = adapter.existsById(DEFAULT_CATEGORY_ID);

        assertThat(exists).isTrue();
        verify(repository).existsById(DEFAULT_CATEGORY_ID);
    }

    @Test
    void shouldThrowPersistenceExceptionWhenExistsByIdFails() {
        doThrow(new DataRetrievalFailureException("Erro na consulta")).when(repository).existsById(DEFAULT_CATEGORY_ID);

        assertThatThrownBy(() -> adapter.existsById(DEFAULT_CATEGORY_ID))
                .isInstanceOf(PersistenceException.class)
                .hasMessage("Erro ao verificar existência da categoria no banco de dados MongoDB.");
    }

    @Test
    void shouldFindAllCategoriesSuccessfully() {
        CategoryDocument document = createCategoryDocumentWithId();
        Category domain = createCategoryWithId();

        when(repository.findAll()).thenReturn(List.of(document));
        when(mapper.toDomain(document)).thenReturn(domain);

        List<Category> results = adapter.findAll();

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getId()).isEqualTo(DEFAULT_CATEGORY_ID);

        verify(repository).findAll();
        verify(mapper).toDomain(document);
    }

    @Test
    void shouldThrowPersistenceExceptionWhenFindAllFails() {
        when(repository.findAll()).thenThrow(new DataRetrievalFailureException("Erro de leitura"));

        assertThatThrownBy(() -> adapter.findAll())
                .isInstanceOf(PersistenceException.class)
                .hasMessage("Erro ao consultar todas as categorias no banco de dados MongoDB.");
    }

    @Test
    void shouldFindCategoryByIdSuccessfully() {
        CategoryDocument document = createCategoryDocumentWithId();
        Category domain = createCategoryWithId();

        when(repository.findById(DEFAULT_CATEGORY_ID)).thenReturn(Optional.of(document));
        when(mapper.toDomain(document)).thenReturn(domain);

        Optional<Category> result = adapter.findById(DEFAULT_CATEGORY_ID);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(DEFAULT_CATEGORY_ID);

        verify(repository).findById(DEFAULT_CATEGORY_ID);
        verify(mapper).toDomain(document);
    }

    @Test
    void shouldReturnEmptyOptionalWhenCategoryNotFoundById() {
        when(repository.findById(DEFAULT_CATEGORY_ID)).thenReturn(Optional.empty());

        Optional<Category> result = adapter.findById(DEFAULT_CATEGORY_ID);

        assertThat(result).isEmpty();
        verify(repository).findById(DEFAULT_CATEGORY_ID);
    }

    @Test
    void shouldThrowPersistenceExceptionWhenFindByIdFails() {
        when(repository.findById(DEFAULT_CATEGORY_ID)).thenThrow(new DataRetrievalFailureException("Erro ao buscar registro"));

        assertThatThrownBy(() -> adapter.findById(DEFAULT_CATEGORY_ID))
                .isInstanceOf(PersistenceException.class)
                .hasMessage("Erro ao buscar categoria pelo ID no banco de dados MongoDB.");
    }
}
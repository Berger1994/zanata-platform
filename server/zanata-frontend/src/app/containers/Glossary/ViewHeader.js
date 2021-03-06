// @ts-nocheck
import React from 'react'
import { Component } from 'react'
import * as PropTypes from 'prop-types'
import {connect} from 'react-redux'
import {debounce, isEmpty} from 'lodash'
import {
  TextInput,
  Link,
  Select
} from '../../components'
import Header from './Header'
import {
  glossaryChangeLocale,
  glossaryFilterTextChanged,
  glossarySortColumn,
  glossaryToggleImportFileDisplay,
  glossaryToggleExportFileDisplay,
  glossaryToggleNewEntryModal,
  glossaryToggleDeleteAllEntriesModal,
  glossaryDeleteAll
} from '../../actions/glossary-actions'
import ImportModal from './ImportModal'
import ExportModal from './ExportModal'
import NewEntryModal from './NewEntryModal'
import DeleteAllEntriesModal from './DeleteAllEntriesModal'
import {getProjectUrl} from '../../utils/UrlHelper'
import Button from 'antd/lib/button'
import 'antd/lib/button/style/css'
import Icon from 'antd/lib/icon'
import 'antd/lib/icon/style/css'
import Row from 'antd/lib/row'
import 'antd/lib/row/style/css'

/**
 * Header for glossary page
 */
class ViewHeader extends Component {
  static propTypes = {
    title: PropTypes.string,
    project: PropTypes.object,
    results: PropTypes.object,
    termCount: PropTypes.number.isRequired,
    statsLoading: PropTypes.bool,
    transLocales: PropTypes.arrayOf(
        PropTypes.shape({
          count: PropTypes.number.isRequired,
          label: PropTypes.string.isRequired,
          value: PropTypes.string.isRequired
        })
    ).isRequired,
    filterText: PropTypes.string,
    selectedTransLocale: PropTypes.string,
    permission: PropTypes.shape({
      canAddNewEntry: PropTypes.bool,
      canUpdateEntry: PropTypes.bool,
      canDeleteEntry: PropTypes.bool
    }).isRequired,
    sort: PropTypes.shape({
      src_content: PropTypes.bool,
      part_of_speech: PropTypes.bool
    }).isRequired,
    deleteAll: PropTypes.object,
    handleTranslationLocaleChange: PropTypes.func,
    handleFilterFieldUpdate: PropTypes.func,
    handleImportFileDisplay: PropTypes.func,
    handleNewEntryDisplay: PropTypes.func,
    handleDeleteAllEntriesDisplay: PropTypes.func,
    handleDeleteAllEntries: PropTypes.func,
    handleSortColumn: PropTypes.func,
    handleSearchCancelClick: PropTypes.func,
    handleExportFileDisplay: PropTypes.func
  }

  currentLocaleCount = () => {
    if (this.props.filterText && this.props.results) {
      return this.props.results
          .filter(result => result.glossaryTerms.length >= 2).length
    } else {
      const selectedTransLocaleObj = this.props.transLocales
          .find((locale) => locale.value === this.props.selectedTransLocale)
      return selectedTransLocaleObj ? selectedTransLocaleObj.count : 0
    }
  }

  localeOptionsRenderer = (op) => {
    return (
      <span className='localeOptions'>
        <span className='localeOptions-label' title={op.label}>
          {op.label}
        </span>
        <span className='localeOptions-value'>
          {op.value}
        </span>
        <span className='localeOptions-count'>
          {op.count}
        </span>
      </span>
    )
  }

  handleClearSearch = () => {
    if (this.searchInput !== null) {
      this.searchInput._onClear()
    }
    this.props.handleSearchCancelClick()
  }

  render () {
    const {
      title,
      project,
      filterText = '',
      termCount,
      statsLoading,
      transLocales,
      selectedTransLocale,
      handleTranslationLocaleChange,
      handleFilterFieldUpdate,
      handleImportFileDisplay,
      handleExportFileDisplay,
      handleNewEntryDisplay,
      handleDeleteAllEntriesDisplay,
      handleDeleteAllEntries,
      handleSortColumn,
      permission,
      sort,
      deleteAll
    } = this.props
    const isEmptyTerms = termCount <= 0
    const currentLocaleCount = this.currentLocaleCount()
    const isReadOnly = !(permission.canAddNewEntry ||
        permission.canUpdateEntry || permission.canDeleteEntry)
    const icon = isReadOnly && (
      <span title='read-only'>
        <Icon type='lock' className='s1 lock mr3' />
      </span>)
    const showDeleteAll = permission.canDeleteEntry && !isEmptyTerms

    const projectUrl = project && getProjectUrl(project.id)

    const projectLink = project && (
      <div className='ml3 projectLink'>
        <Link icon='project' link={projectUrl} useHref>
          <Row>
            <Icon type='folder-open' className='txt-muted mr1' />
            <span className='hidden-lesm'>{project.name}</span>
          </Row>
        </Link>
      </div>
    )

    /* eslint-disable max-len, react/jsx-no-bind, no-return-assign */
    return (
      <Header className='header-glossary' title={title} icon={icon}
        extraHeadingElements={projectLink}
        extraElements={(
          <div className='u-flexRowCenter items-end'>
            <TextInput
              className='textInput'
              ref={(ref) => this.searchInput = ref}
              type='search'
              placeholder='Search Terms…'
              accessibilityLabel='Search Terms'
              defaultValue={filterText}
              onChange={handleFilterFieldUpdate} />
            <Button className='btn-link' aria-label='button'
              icon='close' title='Cancel search'
              disabled={isEmpty(filterText)}
              onClick={this.handleClearSearch} />
            <div className='inline-flex items-end'>
              {permission.canAddNewEntry && (
                <React.Fragment>
                  <Button className='btn-link' type='button'
                    aria-label='button'
                    onClick={() => handleImportFileDisplay(true)}>
                    <Row>
                      <Icon type='upload' className='s1' />
                      <span className='hidden-lesm'>Import</span>
                    </Row>
                  </Button>
                  <ImportModal />
                </React.Fragment>)}

              {permission.canDownload && !isEmptyTerms && (
                <React.Fragment>
                  <Button className='btn-link' type='button'
                    aria-label='button'
                    onClick={() => handleExportFileDisplay(true)}>
                    <Row>
                      <Icon type='export' className='s1' />
                      <span className='hidden-lesm'>Export</span>
                    </Row>
                  </Button>
                  <ExportModal />
                </React.Fragment>)}

              {permission.canAddNewEntry && (
                <React.Fragment>
                  <Button className='btn-link' aria-label='button'
                    onClick={() =>
                        handleNewEntryDisplay(true)}>
                    <Row>
                      <Icon type='plus' className='s1' />
                      <span className='hidden-lesm'>New</span>
                    </Row>
                  </Button>
                  <NewEntryModal />
                </React.Fragment>)}

              {showDeleteAll && (
                <React.Fragment>
                  <DeleteAllEntriesModal show={deleteAll.show}
                    isDeleting={deleteAll.isDeleting}
                    handleDeleteAllEntriesDisplay={
                      handleDeleteAllEntriesDisplay}
                    handleDeleteAllEntries={handleDeleteAllEntries} />
                </React.Fragment>)}
            </div>
          </div>
        )}>
        <div className='glossaryTable'>
          <table>
            <tbody>
              <tr className='tr-flex1'>
                <td className='td-3'
                  onClick={() => handleSortColumn('src_content')}>
                  <Button className='btn-link' aria-label='button'>
                    <Row>
                      {'src_content' in sort
                          ? (sort.src_content === true)
                              ? <Icon name='chevron-down' className='s1' />
                              : <Icon name='chevron-up' className='s1' />
                          : ''}
                      <Icon type='book' className='s1 txt-neutral' />
                      <span>
                      English (United States)
                      </span>
                      <span className='txt-muted ml1'>{termCount}</span>
                    </Row>
                  </Button>
                </td>
                <td className='languageSelect td-3'>
                  <Select
                    name='language-selection'
                    placeholder={statsLoading
                        ? 'Loading…' : 'Select a language…'}
                    className='inputFlex'
                    isLoading={statsLoading}
                    value={selectedTransLocale}
                    options={transLocales}
                    pageSize={20}
                    optionRenderer={this.localeOptionsRenderer}
                    onChange={handleTranslationLocaleChange}
                  />
                  {selectedTransLocale &&
                  (<span className='hidden-xs mt2 ml1'>
                    <Icon type='global' className='s1 txt-neutral' />
                    <span className='txt-muted'>
                      {currentLocaleCount}
                    </span>
                  </span>
                  )}
                </td>
                <td className='td-1'
                  onClick={() => handleSortColumn('part_of_speech')}>
                  <Button className='btn-link ml3' aria-label='button'>
                    <Row className='ml4'>
                      {'part_of_speech' in sort
                          ? (sort.part_of_speech === true)
                              ? <Icon type='down'
                                className='s1 iconChevron' />
                              : <Icon type='up'
                                className='s1 iconChevron' />
                          : ''}
                      <span>
                      Part of Speech
                      </span>
                    </Row>
                  </Button>
                </td>
                <td className='td-1' />
              </tr>
            </tbody>
          </table>
        </div>
      </Header>
      /* eslint-enable max-len, react/jsx-no-bind, no-return-assign */
    )
  }
}

const mapStateToProps = (state) => {
  const {
    stats,
    statsLoading,
    termCount,
    filter,
    permission,
    sort,
    deleteAll,
    project
  } = state.glossary
  // FIXME probably out of date, needs the one that was passed as props
  const query = state.routing.locationBeforeTransitions.query
  return {
    termCount,
    statsLoading,
    transLocales: stats.transLocales,
    filterText: filter,
    selectedTransLocale: query.locale,
    permission,
    sort,
    deleteAll,
    project
  }
}

const mapDispatchToProps = (dispatch) => {
  const updateFilter = debounce((val) =>
      dispatch(glossaryFilterTextChanged(val)), 200)

  return {
    handleTranslationLocaleChange: (selectedLocale) =>
        dispatch(
            glossaryChangeLocale(selectedLocale ? selectedLocale.value : '')
        ),
    handleFilterFieldUpdate: (event) => {
      updateFilter(event.target.value || '')
    },
    handleSearchCancelClick: (event) => {
      updateFilter('')
    },
    handleSortColumn: (col) => dispatch(glossarySortColumn(col)),
    handleImportFileDisplay: (display) =>
        dispatch(glossaryToggleImportFileDisplay(display)),
    handleExportFileDisplay: (display) =>
        dispatch(glossaryToggleExportFileDisplay(display)),
    handleNewEntryDisplay: (display) =>
        dispatch(glossaryToggleNewEntryModal(display)),
    handleDeleteAllEntriesDisplay: (display) =>
        dispatch(glossaryToggleDeleteAllEntriesModal(display)),
    handleDeleteAllEntries: () => dispatch(glossaryDeleteAll())
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(ViewHeader)

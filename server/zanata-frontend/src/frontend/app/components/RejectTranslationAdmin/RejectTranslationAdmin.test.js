/* global jest describe it expect */
jest.disableAutomock()

import React from 'react'
import ReactDOM from 'react-dom'
import ReactDOMServer from 'react-dom/server'
import TestUtils from 'react-addons-test-utils'
import RejectTranslationAdmin from '.'

describe('RejectTranslationAdmin Test', () => {
  it('can render markup', () => {
    const clickFun = function (e) {}
    const actual = ReactDOMServer.renderToStaticMarkup(
      <RejectTranslationAdmin
        fancy={false}
        onClick={clickFun} />)
    const expected = ReactDOMServer.renderToStaticMarkup(
      <div>TODO write a test for markup.</div>
    )
    expect(actual).toEqual(expected)
  })

  it('can render fancy markup', () => {
    const clickFun = function (e) {}
    const actual = ReactDOMServer.renderToStaticMarkup(
      <RejectTranslationAdmin
        fancy
        onClick={clickFun} />)
    const expected = ReactDOMServer.renderToStaticMarkup(
      <div>TODO write a test for markup.</div>
    )
    expect(actual).toEqual(expected)
  })

  it('can be clicked', () => {
    let clickEvent = 'not called'
    const clickFun = function (e) {
      clickEvent = e.value
    }

    const component = TestUtils.renderIntoDocument(
      <RejectTranslationAdmin fancy onClick={clickFun} />
    )
    // simulate click event
    TestUtils.Simulate.click(
      ReactDOM.findDOMNode(component).querySelector('button'),
      {value: 'clicked'})
    expect(clickEvent).toEqual('clicked',
      'RejectTranslationAdmin click event should be fired')
  })
})
